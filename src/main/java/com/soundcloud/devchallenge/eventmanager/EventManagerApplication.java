package com.soundcloud.devchallenge.eventmanager;

import com.soundcloud.devchallenge.eventmanager.processor.EventProcessor;
import com.soundcloud.devchallenge.eventmanager.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventManagerApplication {

    private static final Logger LOG = LoggerFactory.getLogger(EventManagerApplication.class);

    /**
     * This is the main entry point of the event manager. It will start the application and it will be able to register
     * users and an event stream.
     *
     * @param args - arguments for the application. args[1] can be used to set the maxEventSourceBatchSize for a custom
     *             setup that differs from the default configuration of 100
     */
    public static void main(String[] args) {

        NotificationService notificationService = new NotificationService();

        EventProcessor eventProcessor = new EventProcessor(notificationService);

        if (args.length != 0) {
            if (args[0] != null && args[0].matches("^\\d+$")) {
                eventProcessor.setMaxEventBatchSize(Integer.valueOf(args[0]));
            }
        }

        LOG.info("========================================================");
        LOG.info("Starting Event Manager.");
        if (eventProcessor.getMaxEventBatchSize() == 100) {
            LOG.info("Setup for default configured follower-maze-2.0.jar.");
            LOG.info("========================================================");
        } else {
            LOG.info("Setup for custom configured follower-maze-2.0.jar.");
            LOG.info("    maxEventBatchSize set to: {}", eventProcessor.getMaxEventBatchSize());
            LOG.info("========================================================");
        }
        LOG.info("Starting event processor.");

        eventProcessor.startProcessing();

        LOG.info("Processing done.");
        LOG.info("========================================================");

        notificationService.closeStreams();
    }
}