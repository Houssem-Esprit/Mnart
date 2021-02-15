package com.Syrine.mnart.Utils;

import com.Syrine.mnart.Models.User;

import io.socket.client.Socket;

public class Session {

private static Session instance;
private User user;
private Socket socket;


    public Session() {

    }

public static Session getInstance(){

        if (instance==null){
            instance = new Session();
        }
        return  instance;
}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
