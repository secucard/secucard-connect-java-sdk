package com.secucard.connect.client;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.Serializable;
import java.util.Map;

public class OfflineMessage implements Serializable {
    public String corrId;
    public String body;
    public Map<String, String> header;
    public String destination;
    public String returnType;
    public int timeoutSec;
}
