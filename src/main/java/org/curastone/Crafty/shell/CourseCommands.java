package org.curastone.Crafty.shell;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.web.client.RestTemplate;

@ShellComponent
public class CourseCommands {

    @Autowired
    private RestTemplate restTemplate;

    private static final String BASE_URL = "http://localhost:8081";

    @ShellMethod("Create a new course.")
    public String createCourse(@ShellOption String topic, @ShellOption String type) {
        String url = BASE_URL + "/course";
        String requestBody = "{\"topic\": \"" + topic + "\", \"type\": \"" + type + "\"}";
        return restTemplate.postForObject(url, requestBody, String.class);
    }

    @ShellMethod("Submit a new step.")
    public String submitStep(@ShellOption String courseId, @ShellOption String stepType, @ShellOption String parameters) {
        String url = BASE_URL + "/step";
        String requestBody = "{\"courseId\": \"" + courseId + "\", \"stepType\": \"" + stepType + "\", \"parameters\": \"" + parameters + "\"}";
        return restTemplate.postForObject(url, requestBody, String.class);
    }

    @ShellMethod("Get the status of a step.")
    public String getStepStatus(@ShellOption String stepId) {
        String url = BASE_URL + "/step/" + stepId;
        return restTemplate.getForObject(url, String.class);
    }
}

