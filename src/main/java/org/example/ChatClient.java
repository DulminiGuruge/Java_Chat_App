package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


/**
 * client to interact with server
 */
public class ChatClient {
    public static void main(String[] args)throws IOException {

        final int PORT = 8888;
        Socket s = new Socket("localhost",PORT);
        InputStream instream =s.getInputStream();
        OutputStream outputstream=s.getOutputStream();
        Scanner in = new Scanner(instream);
        PrintWriter out= new PrintWriter(outputstream);

        /**
         * some commands sent by the client
         */
        String command="LOGIN John\n";
        System.out.print("Sending: "+ command);
        out.print(command);
        out.flush();
        String response=in.nextLine();
        System.out.println("Receiving: "+ response);

        command="CHAT Hi how are you\n";
        System.out.print("Sending: "+ command);
        out.print(command);
        out.flush();
        response=in.nextLine();
        System.out.println("Receiving: "+ response);

        command = "QUIT\n";
        System.out.print("Sending: "+command);
        out.print(command);
        out.flush();

        s.close();
    }

}