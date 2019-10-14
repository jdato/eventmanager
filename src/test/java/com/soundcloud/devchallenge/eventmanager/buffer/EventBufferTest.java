package com.soundcloud.devchallenge.eventmanager.buffer;

import org.junit.Assert;
import org.junit.Test;

public class EventBufferTest {

    @Test
    public void testEventBufferConfiguration() {
        EventBuffer eventBuffer = new EventBuffer();
        Assert.assertEquals(0, eventBuffer.getBufferWritePosition());
    }

    @Test
    public void testEventBufferWriteAndRead() {
        EventBuffer eventBuffer = new EventBuffer();
        eventBuffer.writeToBuffer("1|F|1|2");
        Assert.assertEquals(1, eventBuffer.getBufferWritePosition());
        Assert.assertEquals("1|F|1|2" ,eventBuffer.readFromBuffer(1));
    }
}
