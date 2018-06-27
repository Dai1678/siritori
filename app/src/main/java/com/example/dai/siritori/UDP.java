package com.example.dai.siritori;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDP {

    private UDPServer udpServer;
    private DatagramSocket socket = null;

    UDP(){}

    UDP(UDPServer udpServer){
        this.udpServer = udpServer;
    }

    public void send(String host, int port, String data){
        new SendProcess(host, port, data).start();
    }

    public void boot(int port){
        try{
            if (socket == null){
                socket = new DatagramSocket(port);
                new ReceiveProcess().start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void shutdown(){
        try{
            if (socket != null){
                socket.close();
                socket = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    class SendProcess extends Thread{
        private String host;
        private int port;
        private String data;

        SendProcess(String host, int port, String data){
            this.host = host;
            this.port = port;
            this.data = data;
        }

        public void run(){
            try{
                InetAddress inetAddress = InetAddress.getByName(host);
                DatagramSocket socket = new DatagramSocket();
                DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getBytes().length, inetAddress, port);

                socket.send(packet);
                socket.close();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    class ReceiveProcess extends Thread{
        public void run(){
            try{
                byte buf[] = new byte[1024*128];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                while (true){
                    socket.receive(packet);

                    InetAddress inetAddress = packet.getAddress();
                    String host = inetAddress.toString();
                    int port = packet.getPort();
                    String data = new String(packet.getData(), 0, packet.getLength());

                    udpServer.receive(host, port, data);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

interface UDPServer {
    void receive(String host, int port, String data);
}
