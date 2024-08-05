package org.curastone.Crafty.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import org.bson.types.ObjectId;
import org.curastone.Crafty.dao.util.ObjectIdSerializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chapters")
public class Chapter {
  @Id
  @JsonProperty("id")
  @JsonSerialize(using = ObjectIdSerializer.class)
  private ObjectId id;

  @JsonProperty("courseId")
  private ObjectId courseId;

  @JsonProperty("title")
  private String title;

  @JsonProperty("slideIds")
  private List<ObjectId> slideIds;

  // Getters and Setters
  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public ObjectId getCourseId() {
    return courseId;
  }

  public void setCourseId(ObjectId courseId) {
    this.courseId = courseId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<ObjectId> getSlideIds() {
    return slideIds;
  }

  public void setSlideIds(List<ObjectId> slideIds) {
    this.slideIds = slideIds;
  }
}
