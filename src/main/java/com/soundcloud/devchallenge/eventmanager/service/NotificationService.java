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

/**
 * This service takes care of all notifications to the users. It stores the info for the network communication and
 * contains all the methods to communicate with the users.
 */
public class NotificationService implements Runnable {

    private Logger LOG = LoggerFactory.getLogger(NotificationService.class);
    private static final int CLIENT_SERVER_PORT = 9099;

    private List<Integer> connectedUsers = new ArrayList<>();
    private Map<Integer, BufferedWriter> userWriters = new HashMap<>();
    private ServerSocket userClientServerSocket;

    /**
     * Runnable method that creates a socket for each users that registers at the CLIENT_SERVER_PORT.
     * Will run in a separate thread.
     */
    @Override
    public void run() {
        try {
            userClientServerSocket = new ServerSocket(CLIENT_SERVER_PORT);

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
            LOG.info("Connections closed.");
        }
    }

    /**
     * Generic notification method to send a message to a user.
     *
     * @param id      - Id of a user
     * @param payload - Message
     */
    private void notify(Integer id, String payload) {
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

    /**
     * Notifies a user that has been followed by another user.
     *
     * @param followedUser - Id of the user that has been followed
     * @param payload      - Message
     */
    public void notifyFollowed(Integer followedUser, String payload) {
        if (connectedUsers.contains(followedUser)) {
            notify(followedUser, payload);
        }
    }

    /**
     * Notifies all the users in a list with a certain message.
     *
     * @param followers - List of users
     * @param payload   - Message
     */
    public void notifyAllFollowers(List<Integer> followers, String payload) {
        if (followers != null) {
            followers.forEach(follower -> {
                if (connectedUsers.contains(follower)) {
                    notify(follower, payload);
                }
            });
        }
    }

    /**
     * Sends a private message to a user.
     *
     * @param receiver - Id of user that should receive message
     * @param payload  - Message
     */
    public void sendPrivateMessage(Integer receiver, String payload) {
        notify(receiver, payload);
    }

    /**
     * Sends a message to all connected users.
     *
     * @param payload - Message
     */
    public void broadcast(String payload) {
        connectedUsers.forEach(user -> notify(user, payload));
    }

    /**
     * Closes all user related streams.
     */
    public void closeStreams() {
        userWriters.forEach((user, writer) -> {
            try {
                writer.close();
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        });
        try {
            userClientServerSocket.close();
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }
}
