package test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class UDPTest {
    // Серверный UDP-сокет запущен на этом порту
    public final static int SERVER_PORT = 1234;

    public static void main(String[] args) throws IOException {
        try {
            DatagramSocket serverSocket = new DatagramSocket();
            /* Создайте буферы для хранения отправляемых и получаемых данных.
            Они временно хранят данные в случае задержек связи */
            byte[] receivingDataBuffer = new byte[1024];
            byte[] sendingDataBuffer = "Hello".getBytes(StandardCharsets.UTF_8);

            // Ip сервера
            InetAddress serverAddress = InetAddress.getByName("127.0.0.1");

            // Создайте новый UDP-пакет с данными, чтобы отправить их серверу
            DatagramPacket outputPacket = new DatagramPacket(
                    sendingDataBuffer, sendingDataBuffer.length,
                    serverAddress, SERVER_PORT
            );

            // Отправьте пакет серверу
            serverSocket.send(outputPacket);

            /* Создайте экземпляр UDP-пакета для хранения клиентских данных с использованием буфера для полученных данных */
            DatagramPacket inputPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
            System.out.println("Waiting for a client to connect...");

            // Получите данные от клиента и сохраните их в inputPacket
            serverSocket.receive(inputPacket);

            // Выведите на экран отправленные клиентом данные
            String receivedData = new String(inputPacket.getData());
            System.out.println("Sent from the client: "+receivedData);

            // Закройте соединение сокетов
            serverSocket.close();
        }
        catch (SocketException e){
            e.printStackTrace();
        }
    }
}
