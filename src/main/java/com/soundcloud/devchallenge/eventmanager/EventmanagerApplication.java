package com.soundcloud.devchallenge.eventmanager;

import com.soundcloud.devchallenge.eventmanager.processor.EventProcessor;
import com.soundcloud.devchallenge.eventmanager.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventmanagerApplication {

    private static final Logger LOG = LoggerFactory.getLogger(EventmanagerApplication.class);

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