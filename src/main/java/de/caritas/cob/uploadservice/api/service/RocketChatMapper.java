package de.caritas.cob.uploadservice.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.caritas.cob.uploadservice.api.service.dto.MethodCall;
import de.caritas.cob.uploadservice.api.service.dto.MethodMessageWithParamMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RocketChatMapper {

  @SuppressWarnings("java:S2245")
  // Using pseudorandom number generators (PRNGs) is security-sensitive
  private static final Random random = new Random();

  private final ObjectMapper objectMapper;

  public MethodCall deleteMessageOf(String messageId) {
    var params = Map.of("_id", messageId);

    var message = new MethodMessageWithParamMap();
    message.setParams(List.of(params));
    message.setId(random.nextInt(100));
    message.setMethod("deleteMessage");

    var deleteMessage = new MethodCall();
    try {
      var messageString = objectMapper.writeValueAsString(message);
      deleteMessage.setMessage(messageString);
    } catch (JsonProcessingException e) {
      log.error("Serializing {} did not work.", message);
    }

    return deleteMessage;
  }

  public MethodCall e2eUpdateMessage(String messageId) {
    var params = Map.of("_id", messageId, "t", "e2e");

    var message = new MethodMessageWithParamMap();
    message.setParams(List.of(params));
    message.setId(random.nextInt(100));
    message.setMethod("updateMessage");

    var updateMessage = new MethodCall();
    try {
      var messageString = objectMapper.writeValueAsString(message);
      updateMessage.setMessage(messageString);
    } catch (JsonProcessingException e) {
      log.error("Serializing {} did not work.", message);
    }

    return updateMessage;
  }
}
