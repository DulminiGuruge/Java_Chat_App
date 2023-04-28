package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Execute commands from the socket
 */
public class Chat implements Runnable{

    private Socket socket;
    private Scanner in;
    private PrintWriter out;

    /**
     * Construct a Chat object which executes commands from the clients
     * @param socketNo the socket
     */
    public Chat(Socket socketNo) {
        socket=socketNo;
    }

    @Override
    public void run() {
        try{
            try{
                in=new Scanner(socket.getInputStream());
                out=new PrintWriter(socket.getOutputStream());
                findCommand();

            }finally {
                socket.close();
            }

        }catch(IOException exception){
            exception.printStackTrace();
        }


    }

    /**
     * QUIT if the client says QUIT otherwise call the function to execute commands
     */

    private void findCommand() {
        while(true){
            if(!in.hasNext()){
                return;
            }
            String command = in.next();
            if(command.equals("QUIT")){
                return;
            }else{
                executeCommand(command);
            }
        }
    }

    /***
     * Execute command sent by the client
     * @param command command to execute
     */

    private void executeCommand(String command) {


        if(command.equals("LOGIN")){
            String name=in.nextLine();
            System.out.println(name + " joined the chat");
            out.println("Hi " + name +", Welcome!");
            out.flush();


        }else if(command.equals("CHAT")){
            String message=in.nextLine();
            System.out.println( message + " ");
            out.println("message sent");
            out.flush();
        }

    }
}