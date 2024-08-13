package org.curastone.Crafty.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.bson.types.ObjectId;
import org.curastone.Crafty.dao.util.ObjectIdSerializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "subscriptions")
public class Subscription {
  @Id
  @JsonProperty("id")
  @JsonSerialize(using = ObjectIdSerializer.class)
  private ObjectId id;

  @JsonProperty("userId")
  private String userId;

  @JsonProperty("type")
  private String type; // Basic, Plus, Premium
}
