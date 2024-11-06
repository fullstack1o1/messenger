package net.samitkumar.messenger.service;

import lombok.RequiredArgsConstructor;
import net.samitkumar.messenger.model.InstantMessageRequest;
import net.samitkumar.messenger.model.InstantMessageResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InstantMessageService {
    final SimpMessagingTemplate simpMessagingTemplate;
    private final static String PRIVATE_QUEUE_ENDPOINT = "/queue/private";

    public InstantMessageResponse sendInstantMessage(InstantMessageResponse.Type type, Long fromUserId, String toUserName, String messageIfAny) {
        return InstantMessageResponse.builder()
                .type(type)
                .payload(InstantMessageResponse.Payload.builder()
                        .type(type)
                        .fromUserId(fromUserId)
                        .message(messageIfAny)
                        .build())
                .build();
    }
}
