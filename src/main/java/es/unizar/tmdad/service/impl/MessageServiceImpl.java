package es.unizar.tmdad.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.unizar.tmdad.adt.MessageListIn;
import es.unizar.tmdad.adt.MessageType;
import es.unizar.tmdad.service.BagOfWordsService;
import es.unizar.tmdad.service.MessageService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final BagOfWordsService bagOfWordsService;

    public MessageServiceImpl(BagOfWordsService bagOfWordsService) {
        this.bagOfWordsService = bagOfWordsService;
    }

    @Override
    public void processMessage(MessageListIn msg) {
        msg.getMessages().forEach(message -> {
            if(Objects.equals(MessageType.FILE, message.getMessageType())){
                log.debug("Ignoring message as it is of type FILE.");
            }else {
                var words = List.of(message.getContent().split(" "));
                this.bagOfWordsService.addWordsToBag(words, parseDate(message.getCreationTimestamp()));
            }
        });
    }

    @SneakyThrows
    private Date parseDate(String s) {
        if(Objects.isNull(s)){
            return null;
        }
        TemporalAccessor ta = DateTimeFormatter.ISO_DATE_TIME.parse(s);
        Instant i = Instant.from(ta);
        return Date.from(i);
    }
}
