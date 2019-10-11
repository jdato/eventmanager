package com.soundcloud.devchallenge.eventmanager;

import com.soundcloud.devchallenge.eventmanager.service.FollowerService;
import com.soundcloud.devchallenge.eventmanager.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class EventmanagerApplication {

    private static Logger LOG = LoggerFactory.getLogger(EventmanagerApplication.class);

    public static void main(String[] args) throws IOException {

        NotificationService notificationService = new NotificationService();
        FollowerService followerService = new FollowerService();

        Thread userThread = new Thread(notificationService);
        userThread.start();

        ServerSocket eventSourceServerSocket = new ServerSocket(9090);
        Socket eventSourceSocket = eventSourceServerSocket.accept();

        BufferedReader reader = new BufferedReader(new InputStreamReader(eventSourceSocket.getInputStream()));

        while (true) {
            if (reader.ready()) {

                String line = reader.readLine();

                LOG.info(line);

                String[] data = line.split("\\|");

                switch (data[1]) {
                    case "B":
                        notificationService.broadcast(line);
                        break;
                    case "S":
                        notificationService.notifyAllFollowers(followerService.getFollowers(Integer.valueOf(data[2])), line);
                        break;
                    case "F":
                        Integer followedUser = Integer.valueOf(data[3]);
                        notificationService.notifyFollowed(followedUser, line);
                        followerService.addFollower(Integer.valueOf(data[2]), followedUser);
                        break;
                    case "U":
                        followerService.removeFollower(Integer.valueOf(data[2]), Integer.valueOf(data[3]));
                        break;
                    case "P":
                        notificationService.sendPrivateMessage(Integer.valueOf(data[3]), line);
                        break;
                }
            }

        }

    }

}