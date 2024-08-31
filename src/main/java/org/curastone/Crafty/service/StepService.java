package org.curastone.Crafty.service;

import java.util.Objects;
import org.curastone.Crafty.model.Step;

public class StepService {

  public static String buildCmd(Step step) {
    String commandLine = "python cli.py ";
    if (Objects.equals(step.getStepType(), "chapter")) {
      commandLine +=
          "step "
              + step.getStepType()
              + " --course_id 1234567"
              + " --topic '"
              + step.getParameters().get("topic")
              + "'";
    } else if (Objects.equals(step.getStepType(), "section")) {
      commandLine +=
          "step "
              + step.getStepType()
              + " --course_id 3d1bd1d60b"
              + " --sections_per_chapter '"
              + step.getParameters().get("sections_per_chapter")
              + "'";
    } else if (Objects.equals(step.getStepType(), "note")) {
      commandLine +=
          "step "
              + step.getStepType()
              + " --course_id 3d1bd1d60b"
              + " --max_note_expansion_words '"
              + step.getParameters().get("max_note_expansion_words")
              + " --chapter '"
              + step.getParameters().get("chapter")
              + "'";
    } else if (Objects.equals(step.getStepType(), "slide")) {
      commandLine +=
          "step "
              + step.getStepType()
              + " --course_id 3d1bd1d60b"
              + " --slides_template_file '"
              + step.getParameters().get("slides_template_file")
              + " --content_slide_pages '"
              + step.getParameters().get("content_slide_pages")
              + "'";
    } else if (Objects.equals(step.getStepType(), "script")) {
      commandLine +=
          "step "
              + step.getStepType()
              + " --course_id 3d1bd1d60b"
              + " --chapter '"
              + step.getParameters().get("chapter")
              + "'";
    } else if (Objects.equals(step.getStepType(), "voice")) {
      commandLine +=
          "step "
              + step.getStepType()
              + " --course_id 3d1bd1d60b"
              + " --chapter '"
              + step.getParameters().get("chapter")
              + "'";
    } else if (Objects.equals(step.getStepType(), "video")) {
      commandLine +=
          "step "
              + step.getStepType()
              + " --course_id 3d1bd1d60b"
              + " --chapter '"
              + step.getParameters().get("chapter")
              + "'";
    } else {
      throw new IllegalArgumentException("Unknown step type: " + step.getStepType());
    }
    return commandLine;
  }
}
