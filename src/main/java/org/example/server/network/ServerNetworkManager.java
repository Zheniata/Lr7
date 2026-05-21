package org.example.server.network;

import org.example.common.Request;
import org.example.common.Response;
import org.example.common.util.LoggerUtil;
import org.example.common.util.SerializationUtil;
import org.example.server.handlers.RequestHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class ServerNetworkManager {
    private ServerSocketChannel serverChannel;
    private int port;
    private Selector selector;
    private static final int BUFFER_SIZE = 8192;
    private final Map<SocketChannel, String> clientIds = new ConcurrentHashMap<>();


    private final ExecutorService acceptThreadPool;
    private final ForkJoinPool processThreadPool;
    private final ForkJoinPool sendThreadPool;

    public ServerNetworkManager(int port) {
        this.port = port;
        this.acceptThreadPool = Executors.newCachedThreadPool();
        this.processThreadPool = new ForkJoinPool();
        this.sendThreadPool = new ForkJoinPool();

        System.out.println("Пулы потоков создны");
    }

    /**
     * Запускает сервер: инициализирует каналы и входит в цикл обработки событий.
     * @param handler обработчик запросов (выполняет команды)
     */

    public void start(RequestHandler handler){
        try {
            System.out.println("Запуск сервера на порту " + port);
            System.out.println("Инициализация ServerSocketChannel");

            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.bind(new InetSocketAddress(port));

            selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Сервер запущен и ожидает подключений на порту " + port);
            System.out.println("Selector инициализирован, зарегистрирован OP_ACCEPT");


            while (true) {
                System.out.println("Ожидание от selector...");
                selector.select();

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    try {
                        if (key.isAcceptable()) {
                            handleAccept(key);
                            System.out.println("Получено событие OP_ACCEPT");
                        }
                        if (key.isReadable()) {
                            acceptThreadPool.submit(() -> {
                                try {
                                    handleRead(key, handler);
                                } catch (Exception e) {
                                    System.err.println("Ошибка чтения: " + e.getMessage());
                                    key.cancel();
                                }
                            });
                        }


                    } catch (Exception e) {
                        System.out.println("Ошибка обработки ключа: " + e.getMessage());
                        e.printStackTrace();
                        key.cancel();
                        try { key.channel().close();
                        } catch (IOException ex) {
                            System.out.println("Ошибка при закрытии канала: " + ex.getMessage());
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Критическая ошибка сервера: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("Завершение работы сервера");
            stop();
        }
    }

    /**
     * Обрабатывает новое подключение клиента:
     * принимает соединение, регистрирует канал в Selector на чтение.
     * @param key ключ с серверным каналом
     */

    private void handleAccept(SelectionKey key) throws IOException{
        System.out.println("Обработка нового подключения");
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();

        if (clientChannel != null) {
            clientChannel.configureBlocking(false);
            clientChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(BUFFER_SIZE));
            System.out.println("Новое подключение от клиента: " + clientChannel.getRemoteAddress());

            String clientId = UUID.randomUUID().toString();
            clientIds.put(clientChannel, clientId);

            System.out.println("Клиент зарегистрирован на OP_READ, выделен буфер " + BUFFER_SIZE + " байт");

        }else {
            System.out.println("Не удалось принять подключение (clientChannel == null)");
        }
    }

    /**
     * Читает данные от клиента, десериализует запрос, обрабатывает его
     * и отправляет ответ. Корректно обрабатывает частичные данные из TCP-потока.
     * @param key ключ с клиентским каналом и буфером
     * @param handler обработчик запросов
     */

    private void handleRead(SelectionKey key, RequestHandler handler) throws IOException, ClassNotFoundException {
        System.out.println("Чтение данных от клиента");
        final SocketChannel clientChannel = (SocketChannel) key.channel();
        String clientId = clientIds.get(clientChannel);
        ByteBuffer buffer = (ByteBuffer) key.attachment();

        int bytesRead = clientChannel.read(buffer);
        if (bytesRead == -1) {
            if (clientId != null) {
                handler.removeSession(clientId);
                clientIds.remove(clientChannel);
                System.out.println("Клиент отключился: " + clientChannel.getRemoteAddress() + " sessionId удалена: " + clientId);
            } else {
                System.out.println("Клиент отключился: " + clientChannel.getRemoteAddress());
            }
            key.cancel();
            clientChannel.close();
            return;
        }

        if (bytesRead > 0) {
            System.out.println("Прочитано " + bytesRead + " байт");
            buffer.flip();

            while (true) {

                if (buffer.remaining() < 4) {
                    System.out.println("Неполные данные в буфере (менее 4 байт), ожидание...");
                    break;
                }

                buffer.mark();
                int length = buffer.getInt();

                if (buffer.remaining() >= length) {
                    System.out.println("Полное сообщение получено, размер: " + length + " байт");
                    buffer.reset();
                    buffer.reset();

                    Request request = (Request) SerializationUtil.deserialize(buffer);
                    System.out.println("Получен запрос от " + clientChannel.getRemoteAddress() +
                            ": команда=" + request.getName() +
                            ", аргумент=" + request.getArgument());

                    Future<Response> future = processThreadPool.submit(() -> {
                        return handler.handle(request, clientId);
                    });

                    Response response = null;
                    try {
                        response = future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        System.err.println("Ошибка обработки: " + e.getMessage());
                        response = new Response(false, "Ошибка обработки запроса", null);
                    }

                    System.out.println("Отправка ответа клиенту...");

                    final Response finalResponse = response;
                    sendThreadPool.submit(() -> {
                        try {
                            ByteBuffer responseBuffer = SerializationUtil.serialize(finalResponse);
                            while (responseBuffer.hasRemaining()) {
                                clientChannel.write(responseBuffer);
                            }
                            System.out.println("Ответ отправлен");
                        } catch (IOException e) {
                            System.err.println("Ошибка отправки ответа: " + e.getMessage());
                        }
                    });

                } else {
                    buffer.reset();
                    break;
                }
            }
            buffer.compact();
        }
    }

    /**
     * Останавливает сервер и закрывает все ресурсы.
     */


    public void stop(){
        System.out.println("Завершение работы сервера...");

        acceptThreadPool.shutdown();
        processThreadPool.shutdown();
        sendThreadPool.shutdown();

        try {
            if (!acceptThreadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                acceptThreadPool.shutdownNow();
            }
            if (!processThreadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                processThreadPool.shutdownNow();
            }
            if (!sendThreadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                sendThreadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            acceptThreadPool.shutdownNow();
            processThreadPool.shutdownNow();
            sendThreadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        try {
            if (selector != null) {
                selector.close();
                System.out.println("Selector закрыт");
            }
            if (serverChannel != null) {
                serverChannel.close();
                System.out.println("ServerSocketChannel закрыт");
            }
            System.out.println("Сервер остановлен");
        } catch (IOException e) {
            System.out.println("Ошибка при остановке сервера: " + e.getMessage());
            System.err.println("Ошибка при остановке: " + e.getMessage());
        }
    }
}
