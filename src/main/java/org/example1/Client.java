package org.example1;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

// Client class
public class Client
{
    public String username;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(Socket socket,String username) throws IOException {
        try{
            this.socket =socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            this.username = username;
        } catch (IOException e) {
            this.socket.close();
        }
    }


    public static void main(String[] args) throws IOException
    {

        try
        {
            Scanner scn = new Scanner(System.in);

            // getting localhost ip
            InetAddress ip = InetAddress.getByName("localhost");

            // establish the connection with server port 5056
            Socket s = new Socket(ip, 5056);

            // obtaining input and out streams
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            //get the username
            System.out.println(dis.readUTF());
            String userName=scn.nextLine();
            dos.writeUTF(userName);
          //  dos.flush();

            // the following loop performs the exchange of
            // information between client and client handler
            while (true)
            {

                System.out.println(dis.readUTF());
                String choosedOption = scn.nextLine();

                if(choosedOption.equals("1"))
                {
                    dos.writeUTF(choosedOption);
                    //dos.flush();
                    System.out.println(dis.readUTF());
                    //System.out.println("Add a Chatroom name");
                    String chatroomName = scn.nextLine();
                    System.out.println(chatroomName +" ");
                    dos.writeUTF(chatroomName);
                    dos.flush();
                    System.out.println(dis.readUTF());
                    continue;


                }
                else if(choosedOption.equals("2"))
                {
                    dos.writeUTF(choosedOption);
                    dos.flush();
                    System.out.println(dis.readUTF());
                    continue;

                }
                else if(choosedOption.equals("3")) {
                    dos.writeUTF(choosedOption);
                    dos.flush();
                    System.out.println(dis.readUTF());
                    //get the chat room name
                    String chatroomName = scn.nextLine();
                    dos.writeUTF(chatroomName);
                    dos.flush();

                    // create a new thread to receive messages from the server
                    AtomicBoolean shouldRun = new AtomicBoolean(true);
                    Thread receiveThread = new Thread(() -> {
                        try {
                            while (shouldRun.get()) {
                                String message = dis.readUTF();
                                System.out.println(message);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    receiveThread.start();

                    // loop to send messages from the client to the server
                    while (true) {
                        String message = scn.nextLine();
                        if (message.equals("bye")) {
                            shouldRun.set(false);
                            break;
                        }
                        dos.writeUTF(message);
                        dos.flush();


                    }
                   ;
                    dos.writeUTF("bye");
                    dos.flush();

                    continue;
                }


                // If client sends exit,close this connection
                // and then break from the while loop
                if(choosedOption.equals("4"))
                {
                    dos.writeUTF(choosedOption);
                    dos.flush();
                    System.out.println("Closing this connection : " + s);
                    s.close();

                    break;
                }
                // printing date or time as requested by client
                String received = dis.readUTF();
                System.out.println(received);
            }
            // closing resources
            scn.close();
            dis.close();
            dos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
