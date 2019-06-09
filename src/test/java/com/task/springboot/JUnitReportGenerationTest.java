package com.task.springboot;

import com.task.springboot.model.User;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JUnitReportGenerationTest {

    public static final String REST_SERVICE_URI = "http://localhost:8080/api";
    public static long user_id;

    /* POST */
    @Test
    @Order(1)
    public void createUser() {
        System.out.println("Testing create User API----------");
        RestTemplate restTemplate = new RestTemplate();
        User user = new User();
        user.setName("Sarah");
        user.setAge(51);
        user.setSalary(134);
        URI uri = restTemplate.postForLocation(REST_SERVICE_URI + "/user", user, User.class);
        System.out.println("Location : " + uri.toASCIIString());

    }


    /* GET */
    @Test
    @Order(2)
    public void listAllUsers() {
        System.out.println("Testing listAllUsers API-----------");

        RestTemplate restTemplate = new RestTemplate();
        List<LinkedHashMap<String, Object>> usersMap = restTemplate.getForObject(REST_SERVICE_URI + "/user/", List.class);

        if (usersMap != null) {
            for (LinkedHashMap<String, Object> map : usersMap) {
                System.out.println("User : id=" + map.get("id") + ", Name=" + map.get("name") + ", Age=" + map.get("age") + ", Salary=" + map.get("salary"));
                ;
            }
        } else {
            System.out.println("No user exist----------");
        }
    }

    @Test
    @Order(3)
    public void getUserByName() {
        System.out.println("Testing getUserByName API----------");
        RestTemplate restTemplate = new RestTemplate();
        User user = restTemplate.getForObject(REST_SERVICE_URI + "/userbyname/Sarah", User.class);
        System.out.println(user);
        user_id = user.getId();

    }



    /* GET */
    @Test
    @Order(4)
    public void getUser() {
        System.out.println("Testing getUser API----------");
        RestTemplate restTemplate = new RestTemplate();
        User user = restTemplate.getForObject(REST_SERVICE_URI + "/user/" + user_id, User.class);
        System.out.println(user);
    }


    /* PUT */
    @Test
    @Order(5)
    public void updateUser() {
        System.out.println("Testing update User API----------");
        RestTemplate restTemplate = new RestTemplate();
        User user = new User(1, "Tomy", 33, 70000);
        restTemplate.put(REST_SERVICE_URI + "/user/" + user_id, user);
        System.out.println(user);
    }

    /* DELETE */
    @Test
    @Order(6)
    public void deleteUser() {
        System.out.println("Testing delete User API----------");
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete(REST_SERVICE_URI + "/user/" + user_id);
    }




}