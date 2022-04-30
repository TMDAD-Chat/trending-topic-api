package es.unizar.tmdad.service;

import es.unizar.tmdad.adt.MessageListIn;

import java.io.IOException;

public interface MessageService {
    void processMessage(MessageListIn msg) throws IOException;

}
