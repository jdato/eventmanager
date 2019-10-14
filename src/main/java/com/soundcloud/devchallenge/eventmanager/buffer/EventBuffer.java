package com.soundcloud.devchallenge.eventmanager.buffer;

import java.util.HashMap;
import java.util.Map;

public class EventBuffer {

    private int bufferWritePosition = 0;
    private Map<Integer, String> eventBuffer = new HashMap<>();

    public EventBuffer() {
    }

    public int getBufferWritePosition() {
        return bufferWritePosition;
    }

    public void writeToBuffer(String eventString) {
        bufferWritePosition++;
        eventBuffer.put(Integer.valueOf(eventString.split("\\|")[0]), eventString);
    }

    public String readFromBuffer(Integer eventNumber) {
        return eventBuffer.remove(eventNumber);
    }
}
