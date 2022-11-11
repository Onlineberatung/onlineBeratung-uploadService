package de.caritas.cob.uploadservice.api.service;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import de.caritas.cob.uploadservice.api.exception.httpresponses.InternalServerErrorException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MongoDbService {

  private final @NonNull MongoClient mongoClient;

  /**
   * This method is used to set the t (=type) field of a rocketchat mongodb message object to the
   * value "e2e" for manual flagging of upload/attachment messages as encrypted
   *
   * @param messageId ID of the message
   */
  public void setE2eType(String messageId) {
    MongoCollection<Document> messageCollection = mongoClient.getDatabase("rocketchat").getCollection("rocketchat_message");
    Bson filter = eq("_id", messageId);
    Bson update = set("t", "e2e");
    UpdateResult result = messageCollection.updateOne(filter, update);
    if (!result.wasAcknowledged() || result.getModifiedCount() != 1) {
      throw new InternalServerErrorException(
          new Exception(
              "Problem encountered while trying to set e2e flag for attachment message, result was: "
                  + result),
          LogService::logInternalServerError);
    }
  }

}
