package org.curastone.Crafty.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.curastone.Crafty.dao.util.ObjectIdSerializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = User.UserBuilder.class)
@NoArgsConstructor
@AllArgsConstructor
public class User {
  @Id
  @JsonProperty("id")
  @JsonSerialize(using = ObjectIdSerializer.class)
  private ObjectId id;

  @JsonProperty("firstName")
  private String firstName;

  @JsonProperty("lastName")
  private String lastName;

  @Indexed(unique = true)
  @JsonProperty("email")
  private String email;

  @JsonProperty("password")
  private String password;

  @JsonProperty("openAiToken")
  private String openAiToken;
}
