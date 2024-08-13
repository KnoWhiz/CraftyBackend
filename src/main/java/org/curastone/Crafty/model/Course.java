package org.curastone.Crafty.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.curastone.Crafty.dao.util.ObjectIdSerializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder(toBuilder = true)
// @JsonDeserialize(builder = User.UserBuilder.class)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "courses")
public class Course {

  @Id
  @JsonProperty("id")
  @JsonSerialize(using = ObjectIdSerializer.class)
  private ObjectId id;

  @JsonProperty("userId")
  private ObjectId userId;

  @JsonProperty("type")
  private String type;

  @JsonProperty("topic")
  private String topic;

  @JsonProperty("steps")
  private Map<String, String> steps;
}
