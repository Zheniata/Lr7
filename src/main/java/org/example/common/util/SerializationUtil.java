package org.example.common.util;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * Утилитный класс для сериализации и десериализации объектов.
 * Обеспечивает совместимый формат передачи данных: [4 байта: длина][данные].
 * Поддерживает работу с ByteBuffer (сервер) и потоками (клиент).
 */

public class SerializationUtil {

    /**
     * Сериализует объект в ByteBuffer с префиксом длины.
     * Формат: [4 байта: длина][сериализованные данные].
     *
     * @param obj объект для сериализации (должен реализовывать Serializable)
     * @return ByteBuffer с данными в формате [длина][данные]
     * @throws IOException если произошла ошибка сериализации
     */
    public static ByteBuffer serialize(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.flush();

        byte[] bytes = baos.toByteArray();
        ByteBuffer buffer = ByteBuffer.allocate(4 + bytes.length);
        buffer.putInt(bytes.length);
        buffer.put(bytes);
        buffer.flip();
        return buffer;
    }

    /**
     * Десериализует объект из ByteBuffer, ожидая префикс длины.
     * Читает 4 байта длины, затем ровно столько байт данных.
     *
     * @param buffer буфер с данными в формате [длина][данные]
     * @return десериализованный объект
     * @throws IOException если произошла ошибка чтения
     * @throws ClassNotFoundException если класс объекта не найден
     */

    public static Object deserialize(ByteBuffer buffer) throws IOException, ClassNotFoundException {
        int length = buffer.getInt();
        byte[] bytes = new byte[length];
        buffer.get(bytes);

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();
    }

    /**
     * Сериализует объект и записывает в OutputStream с префиксом длины.
     * Формат: [4 байта: длина][сериализованные данные].
     *
     * @param obj объект для сериализации
     * @param out поток для записи данных
     * @throws IOException если произошла ошибка записи
     */

    public static void serializeToStream(Object obj, OutputStream out) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.flush();
        byte[] bytes = baos.toByteArray();

        out.write(ByteBuffer.allocate(4).putInt(bytes.length).array());
        out.write(bytes);
        out.flush();
    }

    /**
     * Читает и десериализует объект из InputStream, ожидая префикс длины.
     * Корректно обрабатывает частичное чтение из сетевого потока.
     *
     * @param in поток для чтения данных
     * @return десериализованный объект
     * @throws IOException если произошла ошибка чтения или соединение закрыто
     * @throws ClassNotFoundException если класс объекта не найден
     */

    public static Object deserializeFromStream(InputStream in) throws IOException, ClassNotFoundException {

        byte[] lenBytes = new byte[4];
        int read = 0;
        while (read < 4) {
            int r = in.read(lenBytes, read, 4 - read);
            if (r == -1) throw new IOException("Соединение закрыто");
            read += r;
        }
        int length = ByteBuffer.wrap(lenBytes).getInt();

        byte[] data = new byte[length];
        read = 0;
        while (read < length) {
            int r = in.read(data, read, length - read);
            if (r == -1) throw new IOException("Соединение закрыто");
            read += r;
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();
    }
}
