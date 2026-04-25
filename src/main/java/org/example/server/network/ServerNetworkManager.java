package org.example.server.network;

import org.example.common.Request;
import org.example.common.Response;
import org.example.common.util.LoggerUtil;
import org.example.common.util.SerializationUtil;
import org.example.server.handlers.RequestHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Менеджер сетевого взаимодействия для сервера.
 * Принимает подключения клиентов, читает запросы, передаёт их на обработку
 * и отправляет ответы. Использует неблокирующий I/O (Java NIO) с Selector
 * для обслуживания множества клиентов в одном потоке.
 */

public class ServerNetworkManager {
    private ServerSocketChannel serverChannel;
    private int port;
    private Selector selector;
    private static final int BUFFER_SIZE = 8192;
    private static final Logger logger = LoggerUtil.getLogger();

    public ServerNetworkManager(int port) {
        this.port = port;
    }

    /**
     * Запускает сервер: инициализирует каналы и входит в цикл обработки событий.
     * @param handler обработчик запросов (выполняет команды)
     */

    public void start(RequestHandler handler){
        try {
            logger.info("Запуск сервера на порту " + port);
            logger.fine("Инициализация ServerSocketChannel");

            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.bind(new InetSocketAddress(port));

            selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            logger.info("Сервер запущен и ожидает подключений на порту " + port);
            logger.fine("Selector инициализирован, зарегистрирован OP_ACCEPT");


            while (true) {
                logger.finer("Ожидание от selector...");
                selector.select();

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    try {
                        if (key.isAcceptable()) {
                            handleAccept(key);
                            logger.fine("Получено событие OP_ACCEPT");
                        }
                        if (key.isReadable()) {
                            handleRead(key, handler);
                            logger.fine("Получено событие OP_READ");
                        }
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Ошибка обработки ключа: " + e.getMessage(), e);
                        e.printStackTrace();
                        key.cancel();
                        try { key.channel().close();
                        } catch (IOException ex) {
                            logger.log(Level.WARNING, "Ошибка при закрытии канала: " + ex.getMessage());
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Критическая ошибка сервера: " + e.getMessage(), e);
            e.printStackTrace();
        } finally {
            logger.info("Завершение работы сервера");
            stop();
        }
    }

    /**
     * Обрабатывает новое подключение клиента:
     * принимает соединение, регистрирует канал в Selector на чтение.
     * @param key ключ с серверным каналом
     */

    private void handleAccept(SelectionKey key) throws IOException{
        logger.fine("Обработка нового подключения");
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();

        if (clientChannel != null) {
            clientChannel.configureBlocking(false);
            clientChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(BUFFER_SIZE));
            logger.info("Новое подключение от клиента: " + clientChannel.getRemoteAddress());
            logger.fine("Клиент зарегистрирован на OP_READ, выделен буфер " + BUFFER_SIZE + " байт");

        }else {
            logger.warning("Не удалось принять подключение (clientChannel == null)");
        }
    }

    /**
     * Читает данные от клиента, десериализует запрос, обрабатывает его
     * и отправляет ответ. Корректно обрабатывает частичные данные из TCP-потока.
     * @param key ключ с клиентским каналом и буфером
     * @param handler обработчик запросов
     */

    private void handleRead(SelectionKey key, RequestHandler handler) throws IOException, ClassNotFoundException {
        logger.fine("Чтение данных от клиента");
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();

        int bytesRead = clientChannel.read(buffer);
        if (bytesRead == -1) {
            key.cancel();
            clientChannel.close();
            logger.info("Клиент отключился: " + clientChannel.getRemoteAddress());
            return;
        }

        if (bytesRead > 0) {
            logger.finer("Прочитано " + bytesRead + " байт");
            buffer.flip();

            while (true) {

                if (buffer.remaining() < 4) {
                    logger.finer("Неполные данные в буфере (менее 4 байт), ожидание...");
                    break;
                }

                buffer.mark();
                int length = buffer.getInt();

                if (buffer.remaining() >= length) {
                    logger.fine("Полное сообщение получено, размер: " + length + " байт");
                    buffer.reset();
                    buffer.reset();

                    Request request = (Request) SerializationUtil.deserialize(buffer);
                    logger.info("Получен запрос от " + clientChannel.getRemoteAddress() +
                            ": команда=" + request.getName() +
                            ", аргумент=" + request.getArgument());

                    Response response = handler.handle(request);
                    logger.info("Отправка ответа клиенту: " + response.getMessage());
                    logger.fine("Статус ответа: " + (response.isSuccess() ? "успех" : "ошибка"));

                    ByteBuffer responseBuffer = SerializationUtil.serialize(response);
                    while (responseBuffer.hasRemaining()) {
                        clientChannel.write(responseBuffer);
                    }
                    logger.finer("Ответ успешно отправлен");

                } else {
                    logger.finer("Данных недостаточно для полного сообщения, ожидание следующей части");
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
        try {
            if (selector != null) {
                selector.close();
                logger.fine("Selector закрыт");
            }
            if (serverChannel != null) {
                serverChannel.close();
                logger.fine("ServerSocketChannel закрыт");
            }
            logger.info("Сервер остановлен");
        } catch (IOException e) {
            logger.log(Level.WARNING, "Ошибка при остановке сервера: " + e.getMessage(), e);
            System.err.println("Ошибка при остановке: " + e.getMessage());
        }
    }
}
