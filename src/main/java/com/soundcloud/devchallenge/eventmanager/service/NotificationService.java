package com.soundcloud.devchallenge.eventmanager.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationService implements Runnable {

    Logger LOG = LoggerFactory.getLogger(NotificationService.class);

    private List<Integer> connectedUsers = new ArrayList<>();
    private Map<Integer, BufferedWriter> userWriters = new HashMap<>();

    @Override
    public void run() {
        try {
            ServerSocket userClientServerSocket = new ServerSocket(9099);

            while (true) {
                Socket userSocket = userClientServerSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
                String userIdString = reader.readLine();
                Integer id = Integer.valueOf(userIdString);

                connectedUsers.add(id);
                userWriters.put(id, new BufferedWriter(new OutputStreamWriter(userSocket.getOutputStream(), "UTF-8")));
                LOG.info("Connected user {}", id);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notify(Integer id, String payload) {
        if (connectedUsers.contains(id)) {
            try {
                BufferedWriter writer = userWriters.get(id);
                writer.write(payload);
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        } // else, Ignoring notification silently
    }

    public void notifyFollowed(Integer followedUser, String payload) {
        if(connectedUsers.contains(followedUser)) {
            notify(followedUser, payload);
            LOG.info("[F] Notified {} of {}", followedUser, payload);
        }
    }

    public void notifyAllFollowers(List<Integer> followers, String payload) {
        if (followers != null) {
            followers.forEach(follower -> {
                if (connectedUsers.contains(follower)) {
                    notify(follower, payload);
                    LOG.info("[S] Notified {} of {}", follower, payload);
                }
            });
        }
    }

    public void sendPrivateMessage(Integer receiver, String payload) {
        notify(receiver, payload);
        LOG.info("[P] Notified {} of {}", receiver, payload);
    }

    public void broadcast(String payload) {
        connectedUsers.forEach(user -> {
            notify(user, payload);
            LOG.info("[B] Notified {} of {}", user, payload);
        });
    }

    public void closeStreams() {
        userWriters.forEach((user, writer)-> {
            try {
                writer.close();
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        });
    }
}
