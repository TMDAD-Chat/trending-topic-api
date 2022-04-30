package es.unizar.tmdad.adt;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MessageListIn {

    private String requestId;
    private String recipient;
    private RecipientType recipientType;
    private List<MessageIn> messages;

}
