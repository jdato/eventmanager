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
import java.util.HashMap;
import java.util.Map;

public class EventmanagerApplication {

    private static final Logger LOG = LoggerFactory.getLogger(EventmanagerApplication.class);
    private static final int MAX_EVENT_BATCH_SIZE = 100;

    private static NotificationService notificationService;
    private static FollowerService followerService;
    private static Map<Integer, String> eventBuffer = new HashMap<Integer, String>();
    private static int bufferWritePosition = 0;
    private static int bufferReadPosition = 0;
    private static int eventReaderTimeoutInMilliseconds = 5000;

    public static void main(String[] args) throws IOException {

        boolean eventStreamActive = true;
        int remainingEvents = 0;
        int timeOutCounter = 0;

        notificationService = new NotificationService();
        followerService = new FollowerService();

        Thread userThread = new Thread(notificationService);
        userThread.start();

        ServerSocket eventSourceServerSocket = new ServerSocket(9090);
        Socket eventSourceSocket = eventSourceServerSocket.accept();

        BufferedReader reader = new BufferedReader(new InputStreamReader(eventSourceSocket.getInputStream()));

        // Setup buffer
        for (int i = 0; i < (MAX_EVENT_BATCH_SIZE * 2); i++) {
            writeToBuffer(reader.readLine());
        }

        // Process data
        while (true) {
            if (reader.ready()) {
                timeOutCounter = 0;
                //LOG.info("Reader ready.");
                for (int i = 0; i < (MAX_EVENT_BATCH_SIZE * 2); i++) {
                    String eventString = reader.readLine();
                    //LOG.info("Event {}", eventString);
                    if (eventString != null) {
                        writeToBuffer(eventString);
                    } else {
                        eventStreamActive = false;
                        remainingEvents = i;
                        break;
                    }
                }

                if (!eventStreamActive) {
                    break;
                }

                for (int i = bufferWritePosition - (MAX_EVENT_BATCH_SIZE * 4); i < bufferWritePosition - (MAX_EVENT_BATCH_SIZE * 2); i++) {
                    readFromBuffer(i + 1);
                }
            } else {
                if (timeOutCounter < eventReaderTimeoutInMilliseconds) {
                    timeOutCounter++;
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        LOG.error(e.getMessage());
                    }
                } else {
                    break;
                }
            }
        }

        // Tear down buffer
        for (int i = bufferWritePosition - ((MAX_EVENT_BATCH_SIZE * 2) + remainingEvents); i < bufferWritePosition; i++) {
            readFromBuffer(i + 1);
        }

    }

    static void writeToBuffer(String eventString) {
        bufferWritePosition++;
        eventBuffer.put(Integer.valueOf(eventString.split("\\|")[0]), eventString);
    }

    static void readFromBuffer(Integer eventNumber) {
        bufferReadPosition++;
        processEvent(eventBuffer.remove(eventNumber));
    }

    private static void processEvent(String eventString) {

        String[] data = eventString.split("\\|");

        switch (data[1]) {
            case "B":
                notificationService.broadcast(eventString);
                break;
            case "S":
                notificationService.notifyAllFollowers(followerService.getFollowers(Integer.valueOf(data[2])), eventString);
                break;
            case "F":
                Integer followedUser = Integer.valueOf(data[3]);
                notificationService.notifyFollowed(followedUser, eventString);
                followerService.addFollower(Integer.valueOf(data[2]), followedUser);
                break;
            case "U":
                followerService.removeFollower(Integer.valueOf(data[2]), Integer.valueOf(data[3]));
                break;
            case "P":
                notificationService.sendPrivateMessage(Integer.valueOf(data[3]), eventString);
                break;
        }
    }

}