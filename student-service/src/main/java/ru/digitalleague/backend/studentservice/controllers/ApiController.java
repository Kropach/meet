package ru.digitalleague.backend.studentservice.controllers;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.digitalleague.backend.javamodel.entities.Student;

import java.io.IOException;
import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api")
public class ApiController {

    Logger logger = LoggerFactory.getLogger(ApiController.class);
    OkHttpClient okHttpClient = new OkHttpClient();
    @Autowired
    Environment env;

    @GetMapping("/")
    public String getPort(){
        String response = "student service running at port:" + env.getProperty("server.port");
        logger.info(response);
        return response;
    }

    @GetMapping("/student")
    public Student getStudent() throws IOException {
        String person = getResponseFromServer("https://randomuser.me/api");
        Student student = convertStringToStudent(person);
        return student;
    }

    private String getResponseFromServer(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }

    private Student convertStringToStudent(String person){
        JSONObject fullJsonObject = new JSONObject(person)
                .getJSONArray("results")
                .getJSONObject(0);
        Student student = new Student();
        student.setGender(fullJsonObject.getString("gender").substring(0,1));
        student.setEmail(fullJsonObject.getString("email"));

        JSONObject dobJsonObject = fullJsonObject.getJSONObject("dob");
        ZonedDateTime zdt = ZonedDateTime.parse(dobJsonObject.getString("date"));
        student.setDob(zdt.toLocalDate());

        JSONObject nameJsonObject = fullJsonObject.getJSONObject("name");
        student.setFirstName(nameJsonObject.getString("first"));
        student.setLastName(nameJsonObject.getString("last"));

        logger.info(student.toString());
        return student;
    }
}
