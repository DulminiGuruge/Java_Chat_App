package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server that executes simple commands from the client
 */
public class ChatServer {

    public static void main(String[] args) throws IOException {
        final int port=8888;
        ServerSocket server=new ServerSocket(port);
        System.out.println("Waiting for clients to connect ......");

        while(true){
            Socket s=server.accept();
            System.out.println("Client connected.");
            Chat channel=new Chat(s);
            Thread t=new Thread(channel);
            t.start();

        }



    }
}