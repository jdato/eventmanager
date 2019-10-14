package com.soundcloud.devchallenge.eventmanager.processor;

import com.soundcloud.devchallenge.eventmanager.service.NotificationService;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class EventProcessorTest {

    @Test
    public void testEventProcessor() {
        NotificationService notificationService = new NotificationService();
        EventProcessor eventProcessor = new EventProcessor(notificationService);
        eventProcessor.setMaxEventBatchSize(2);
        Assert.assertEquals(2, eventProcessor.getMaxEventBatchSize());

        List<String> events = new ArrayList<>();
        events.add("2|F|60|50\n");
        events.add("1|U|12|9\n");
        events.add("3|B\n");
        events.add("4|P|32|56\n");
        events.add("6|F|40|50\n");
        events.add("5|S|50\n");
        events.add("8|F|50|40\n");
        events.add("7|S|4\n");
        events.add("9|U|60|50\n");
        events.add("10|S|32\n");
        events.add("11|S|24\n");
        events.add("12|S|12\n");

        Thread tester = new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Socket userStream, userStream2;
            try {
                userStream = new Socket("localhost", 9099);

                BufferedWriter userWriter = new BufferedWriter(new OutputStreamWriter(userStream.getOutputStream()));
                userWriter.write("40\n");
                userWriter.flush();

                userStream2 = new Socket("localhost", 9099);

                BufferedWriter userWriter2 = new BufferedWriter(new OutputStreamWriter(userStream2.getOutputStream()));
                userWriter2.write("60\n");
                userWriter2.flush();


                Socket eventStream = new Socket("localhost", 9090);
                BufferedWriter eventWriter = new BufferedWriter(new OutputStreamWriter(eventStream.getOutputStream()));
                events.forEach(event -> {
                    try {
                        eventWriter.write(event);
                        eventWriter.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        tester.start();

        eventProcessor.startProcessing();

        notificationService.closeStreams();
    }

}
