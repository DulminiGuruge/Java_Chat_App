package org.example1;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Chatroom {

    Hashtable<String, ArrayList<ClientHandler>> hashtable = new Hashtable<String, ArrayList<ClientHandler>>();
    private Lock chatLock;
    private Set<String> chatGroups = new HashSet<>();

    public Chatroom() {
        chatLock = new ReentrantLock();
    }


    /*
    * */
    public boolean CreateChatroom(String Chatroomname) {
        chatLock.lock();
        try {
            ArrayList<ClientHandler> chats = new ArrayList<ClientHandler>();

            chatGroups.add(Chatroomname);
            hashtable.put(Chatroomname, chats);

            chatLock.unlock();

            return true;
        } catch (Exception e) {
            chatLock.unlock();

            return false;
        }

    }

    public String ListChatrooms() {
        // String keylist = "";
        return chatGroups.toString();
    }

    public void joinChatroom(String chatroomName, ClientHandler clientThread) {
        chatLock.lock();

        if (hashtable.containsKey(chatroomName)) {
            // Add the new string to the ArrayList associated with the selected key
            ArrayList<ClientHandler> values = hashtable.get(chatroomName);
            values.add(clientThread);
        }

        chatLock.unlock();

    }

    public void leaveChatroom(String chatroomName, ClientHandler userThread) {
        chatLock.lock();

        ArrayList<ClientHandler> values = new ArrayList<ClientHandler>();
        if (hashtable.containsKey(chatroomName)) {
            // Add the new string to the ArrayList associated with the selected key
            values = hashtable.get(chatroomName);
            values.remove(userThread);
        }

        chatLock.unlock();

    }

    public ArrayList<ClientHandler> getMembers(String chatroomName) {
        chatLock.lock();
        ArrayList<ClientHandler> clientThreads = new ArrayList<ClientHandler>();
        if (hashtable.containsKey(chatroomName)) {
            clientThreads = hashtable.get(chatroomName);
        }

        chatLock.unlock();
        return clientThreads;
    }

}


