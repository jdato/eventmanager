package com.soundcloud.devchallenge.eventmanager.buffer;

import java.util.HashMap;
import java.util.Map;

/**
 * The event buffer is necessary because the events can be sent in a randomized order.
 * Therefore a buffer structure is needed to handle the variance and bring the events back in order.
 */
public class EventBuffer {

    private int bufferWritePosition = 0;
    private Map<Integer, String> eventBuffer = new HashMap<>();

    public EventBuffer() {
    }

    /**
     * Returns the current writer position of the buffer.
     *
     * @return writerposition
     */
    public int getBufferWritePosition() {
        return bufferWritePosition;
    }

    /**
     * Writes an event to the buffer.
     *
     * @param eventString - Event that needs to be written on buffer
     */
    public void writeToBuffer(String eventString) {
        bufferWritePosition++;
        eventBuffer.put(Integer.valueOf(eventString.split("\\|")[0]), eventString);
    }

    /**
     * Reads and removes an event from the buffer.
     *
     * @param eventNumber - Event to be read and removed
     * @return - Event that has been read and removed
     */
    public String readFromBuffer(Integer eventNumber) {
        return eventBuffer.remove(eventNumber);
    }
}
