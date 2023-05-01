package org.example1;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

// Server class
public class Server {
    Chatroom chatrooms = new Chatroom();
    private Set<String> userNames = new HashSet<>();
    private Set<ClientHandler> userThreads = new HashSet<>();

    public void execute(ServerSocket ss) throws IOException {
        // running infinite loop for getting
        // client request
        while (true) {
            Socket s = null;

            try {

                // socket object to receive incoming client requests
                s = ss.accept();

                System.out.println("A new client is connected...... ");

                // obtaining input and out streams
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                System.out.println("Assigning new thread for this client...");

                // create a new thread object
                Thread t = new ClientHandler(s, dis, dos, chatrooms, this);
                userThreads.add((ClientHandler) t);

                // Invoking the start() method
                t.start();

            } catch (Exception e) {
                s.close();
                e.printStackTrace();
            }
        }

    }

    void broadcast(String message, ClientHandler excludeUser) throws IOException {
        for (ClientHandler aUser : userThreads) {
            // if (aUser != excludeUser) {
            aUser.sendMessage(message);
            System.out.println("broadcast message");
            // }
        }
    }

    void removeUser(String userName, ClientHandler aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(aUser);
            System.out.println(userName + "left the chat");
        }
    }

    void addUserName(String username) {
        userNames.add(username);
    }

    public static void main(String[] args) throws IOException {

        // server is listening on port 5056
        ServerSocket ss = new ServerSocket(5056);
        System.out.println("Waiting for clients........");
        Server serverObj = new Server();
        serverObj.execute(ss);


    }
}

// ClientHandler class
class ClientHandler extends Thread {

    Server chatServer;

    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;
    Chatroom chatRooms;
    String username = " ";


    // Constructor
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos, Chatroom chatrooms, Server obj) {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        this.chatRooms = chatrooms;
        this.chatServer = obj;
    }

    @Override
    public void run() {
        String received;
        String toreturn;

        try {
            dos.writeUTF("Please enter the username: ");
            dos.flush();
            username = dis.readUTF();

            chatServer.addUserName(username);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (true) {
            try {

                dos.writeUTF("OPTIONS:  " +
                        "1 - Create chat-rooms, " +
                        "2 - List all existing rooms, " +
                        "3 - Join existing chat-rooms, " +
                        "4 - Leave a chat-room");

                // dos.flush();

                // receive the answer from client
                received = dis.readUTF();
                System.out.println(received);
                if (received.equals("1")) {

                    dos.writeUTF("Add a Chatroom name: ");
                    String messageFromClient = dis.readUTF();
                    chatRooms.CreateChatroom(messageFromClient);
                    System.out.println("Chat room " + messageFromClient + " created");
                    dos.writeUTF("Chat room created");
                    dos.flush();
                    continue;

                } else if (received.equals("2")) {

                    dos.writeUTF(chatRooms.ListChatrooms());
                    dos.flush();
                    System.out.println(chatRooms.ListChatrooms());
                    continue;

                } else if (received.equals("3")) {

                    dos.writeUTF("Type Chatroom name: ");
                    String chatroomName = dis.readUTF();
                    chatRooms.joinChatroom(chatroomName, this);
                    ArrayList<ClientHandler> groupMembers = chatRooms.getMembers(chatroomName);
                    dos.flush();

                    for (ClientHandler aUser : groupMembers) {
                        if (aUser != this) {

                            aUser.dos.writeUTF(username + " joined the chat");

                        } else {

                            dos.writeUTF("Hello " + username + " ! ");
                        }
                        dos.flush();
                    }

                    String clientMessage;
                    String serverMessage;

                    while (true) {
                        clientMessage = dis.readUTF();
                        if (clientMessage.equals("bye")) {

                            for (ClientHandler aUser : groupMembers) {
                                if (aUser != this) {

                                    aUser.dos.writeUTF(username + " left " + chatroomName);
                                    dos.flush();

                                } else {

                                    System.out.println(username + " left the chat : " + chatroomName);

                                }
                            }
                            break;

                        } else {
                            serverMessage = "[" + username + "]: " + clientMessage;
                            System.out.println(chatroomName + ": " + serverMessage);

                            for (ClientHandler aUser : groupMembers) {
                                if (aUser != this) {

                                    aUser.dos.writeUTF(serverMessage);
                                    dos.flush();

                                } else {

                                    dos.writeUTF("[you]: " + clientMessage);
                                    dos.flush();
                                }
                            }

                        }

                    }
                    // remove the username from the chat
                    chatRooms.leaveChatroom(chatroomName, this);
                    continue;
                    // dos.writeUTF(username + " left "+ chatroomName);

                } else if (received.equals("4")) {
                    System.out.println("Client " + this.s + " sends exit...");
                    chatServer.removeUser(username, this);
                    //  System.out.println("Closing this connection.");
                    this.s.close();
                    //System.out.println("Connection closed");
                    break;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            // closing resources
            this.dis.close();
            this.dos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to the client.
     */
    void sendMessage(String message) throws IOException {
        dos.writeUTF(message);
        dos.flush();
        System.out.println("Send message");
    }
}
