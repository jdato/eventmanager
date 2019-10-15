package com.soundcloud.devchallenge.eventmanager.processor;

import com.soundcloud.devchallenge.eventmanager.buffer.EventBuffer;
import com.soundcloud.devchallenge.eventmanager.service.FollowerService;
import com.soundcloud.devchallenge.eventmanager.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This processor takes care of the processing work that the application has to perform.
 */
public class EventProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(EventProcessor.class);

    private static final int EVENT_SERVER_PORT = 9090;
    private final int EVENT_READER_TIMEOUT_IN_MILLISECONDS = 3000;
    private int MAX_EVENT_BATCH_SIZE = 100;
    private Thread userThread;

    private ServerSocket eventSourceServerSocket;
    private FollowerService followerService;
    private NotificationService notificationService;
    private EventBuffer eventBuffer;

    public EventProcessor(NotificationService notificationService) {
        try {
            this.eventSourceServerSocket = new ServerSocket(EVENT_SERVER_PORT);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        this.notificationService = notificationService;
        this.followerService = new FollowerService();
        this.eventBuffer = new EventBuffer();
        this.userThread = new Thread(notificationService);
    }

    /**
     * Returns the current maxEventBatchSize.
     * Which defaults to 100
     *
     * @return maxEventBatchSize
     */
    public int getMaxEventBatchSize() {
        return MAX_EVENT_BATCH_SIZE;
    }

    /**
     * Sets the maxEventBatchSize
     *
     * @param maxEventBatchSize - Size of randomized event Batches from event source
     */
    public void setMaxEventBatchSize(int maxEventBatchSize) {
        MAX_EVENT_BATCH_SIZE = maxEventBatchSize;
    }

    /**
     * Starts the processing of data from the data sources (User and Events).
     */
    public void startProcessing() {
        try {
            userThread.start();

            boolean eventStreamActive = true;
            int remainingEvents = 0;
            int timeOutCounter = 0;

            Socket eventSourceSocket = eventSourceServerSocket.accept();

            BufferedReader reader = new BufferedReader(new InputStreamReader(eventSourceSocket.getInputStream()));

            // Setup buffer
            for (int i = 0; i < (MAX_EVENT_BATCH_SIZE * 2); i++) {
                eventBuffer.writeToBuffer(reader.readLine());
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
                            eventBuffer.writeToBuffer(eventString);
                        } else {
                            eventStreamActive = false;
                            remainingEvents = i;
                            break;
                        }
                    }

                    if (!eventStreamActive) {
                        break;
                    }

                    for (int i = eventBuffer.getBufferWritePosition() - (MAX_EVENT_BATCH_SIZE * 4); i < eventBuffer.getBufferWritePosition() - (MAX_EVENT_BATCH_SIZE * 2); i++) {
                        processEvent(eventBuffer.readFromBuffer(i + 1));
                    }
                } else {
                    if (timeOutCounter < EVENT_READER_TIMEOUT_IN_MILLISECONDS) {
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
            for (int i = eventBuffer.getBufferWritePosition() - ((MAX_EVENT_BATCH_SIZE * 2) + remainingEvents); i < eventBuffer.getBufferWritePosition() && i != 0; i++) {
                processEvent(eventBuffer.readFromBuffer(i + 1));
            }

            eventSourceServerSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generic method to process a single event.
     *
     * @param eventString - Event to be processed
     */
    private void processEvent(String eventString) {
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
