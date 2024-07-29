package org.curastone.Crafty.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.curastone.Crafty.dao.util.ObjectIdSerializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = Course.CourseBuilder.class)
@Document(collection = "courses")
public class Course {

  @Id
  @JsonProperty("id")
  @JsonSerialize(using = ObjectIdSerializer.class)
  private final ObjectId id;

  @JsonProperty("userId")
  @JsonSerialize(using = ObjectIdSerializer.class)
  private final ObjectId userId;

  @JsonProperty("courseName")
  private final String courseName;

  @JsonProperty("creationTime")
  private final Instant creationTime;

  @JsonProperty("lastUpdatedTime")
  private final Instant lastUpdatedTime;

  @JsonProperty("type")
  private final CourseType type;

  @JsonProperty("status")
  private final CourseStatus status;

  @Builder.Default
  @JsonProperty("statusTimeline")
  private final List<CourseStatusEvent> statusTimeline = new ArrayList<>();

  @Builder.Default
  @JsonProperty("sections")
  private final List<CourseSection> sections = new ArrayList<>();

  public enum CourseType {
    DEFAULT,
    ZERO_SHOT,
    USER_UPLOADED,
    CLAIMED_ZERO_SHOT,
    CLAIMED_USER_UPLOADED
  }

  @Data
  public static class CourseStatusEvent {

    @JsonProperty("effectiveTime")
    private final Instant effectiveTime;

    @JsonProperty("status")
    private final CourseStatus status;
  }

  public enum CourseStatus {
    /** The course was uploaded earlier and is being processed by the system. */
    PROCESSING,
    /**
     * The course has been processed by the science pipeline successfully, and is ready for the user
     * to start learning.
     */
    READY,
    /** The science pipeline encountered some error processing the course. */
    PROCESSING_ERROR,
    /** The user has started learning the course. */
    STUDYING,
    /** The user has finished learning the course. */
    COMPLETED,
    /** The course has been deleted. */
    DELETED
  }

  @Data
  @Builder(toBuilder = true)
  @JsonDeserialize(builder = PreSignedUrl.PreSignedUrlBuilder.class)
  public static class PreSignedUrl {
    @JsonProperty("preSignedUrl")
    private final String preSignedUrl;
  }

  @Data
  @Builder(toBuilder = true)
  @JsonDeserialize(builder = CourseSection.CourseSectionBuilder.class)
  public static class CourseSection {
    public static final Comparator<CourseSection> COURSE_SECTION_COMPARATOR =
        Comparator.comparing(CourseSection::getIndex).thenComparing(CourseSection::getTitle);

    @JsonProperty("index")
    private final Integer index;

    @JsonProperty("title")
    private final String title;

    @JsonProperty("lastQuizId")
    @JsonSerialize(using = ObjectIdSerializer.class)
    private final ObjectId lastQuizId;
  }
}
