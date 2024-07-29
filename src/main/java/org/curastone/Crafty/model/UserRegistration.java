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
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = UserRegistration.UserRegistrationBuilder.class)
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistration {
  @Id
  @JsonProperty("id")
  @JsonSerialize(using = ObjectIdSerializer.class)
  private ObjectId id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("email")
  private String email;
}
