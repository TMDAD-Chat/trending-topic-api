package es.unizar.tmdad.listener;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import es.unizar.tmdad.adt.*;
import es.unizar.tmdad.service.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

import java.io.*;

@Slf4j
@Component
public class MessageListener {

    private final MessageService messageService;
    private final ObjectMapper objectMapper;

    public MessageListener(MessageService messageService, ObjectMapper objectMapper) {
        this.messageService = messageService;
        this.objectMapper = objectMapper;
    }

    public void apply(String input) throws JsonProcessingException {
        MessageListIn msg = objectMapper.readValue(input, MessageListIn.class);
        this.apply(msg);
    }

    public void apply(MessageListIn messageInFlux) {
        log.info("Processing msg {}.", messageInFlux);
        try {
            messageService.processMessage(messageInFlux);
        } catch (IOException e) {
            log.info("Error while processing message {}", messageInFlux);
        }
    }
}
