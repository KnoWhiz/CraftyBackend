package org.curastone.Crafty.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.curastone.Crafty.dao.util.ObjectIdSerializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Builder(toBuilder = true)
// @JsonDeserialize(builder = User.UserBuilder.class)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "steps")
public class Step {
  @Id
  @JsonProperty("id")
  @JsonSerialize(using = ObjectIdSerializer.class)
  private ObjectId id;

  @JsonProperty("courseId")
  private ObjectId courseId;

  @JsonProperty("stepType")
  private String stepType; // CHAPTER, SECTION, NOTE, SLIDE, SCRIPT, VOICE_OVER, VIDEO

  @JsonProperty("stepStatus")
  private String stepStatus; // IN QUEUE, IN PROGRESS, SUCCEED, FAILED

  @JsonProperty("parameters")
  private Map<String, String> parameters;

  @JsonProperty("result")
  private String result;

  @JsonProperty("jobId")
  private String jobId;
}
