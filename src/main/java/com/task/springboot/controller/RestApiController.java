package com.task.springboot.controller;

import java.util.List;


import com.task.springboot.model.UserWrapper;
import com.task.springboot.service.UserService;
import com.task.springboot.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.task.springboot.model.User;
import com.task.springboot.util.CustomErrorType;

import static com.task.springboot.config.RabbitConfiguration.*;

@RestController
@RequestMapping("/api")
public class RestApiController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public static final Logger logger = LoggerFactory.getLogger(RestApiController.class);

    @Autowired
    UserService userService; //Service which will do all data retrieval/manipulation work


    // -------------------Retrieve All Users---------------------------------------------

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseEntity<List<User>> listAllUsers() {
        List<User> users = (List<User>) rabbitTemplate.convertSendAndReceive(QUEUE_GET_ALL_USERS, "GetAllUsers");
        if (users.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
            // You many decide to return HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<List<User>>(users, HttpStatus.OK);
    }

    // -------------------Retrieve Single User------------------------------------------

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@PathVariable("id") long id) {
        logger.info("Fetching User with id {}", id);
        UserWrapper userWrapper = (UserWrapper) rabbitTemplate.convertSendAndReceive(QUEUE_USER_FIND_BY_ID, Long.toString(id));
        if (userWrapper.getUser() == null) {
            logger.error("User with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("User with id " + id
                    + " not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<User>(userWrapper.getUser(), HttpStatus.OK);
    }

    // -------------------Retrieve Single User by name-------------------------------------------

    @RequestMapping(value = "/userbyname/{name}", method = RequestMethod.GET)
    public ResponseEntity<?> getUserByName(@PathVariable("name") String name) {
        logger.info("Fetching User with name {}", name);
        UserWrapper userWrapper = (UserWrapper) rabbitTemplate.convertSendAndReceive(QUEUE_USER_FIND_BY_NAME, name);
        if (userWrapper.getUser() == null) {
            logger.error("User with name {} not found.", name);
            return new ResponseEntity(new CustomErrorType("User with name " + name
                    + " not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<User>(userWrapper.getUser(), HttpStatus.OK);

    }



    // -------------------Create a User-------------------------------------------

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@RequestBody User user, UriComponentsBuilder ucBuilder) {
        UserWrapper userWrapper = (UserWrapper) rabbitTemplate.convertSendAndReceive(QUEUE_USER_FIND_BY_NAME, user.getName());
        User check_user =userWrapper.getUser();
        if (check_user != null) {
            logger.error("Unable to create. A User with name {} already exist", user.getName());
            return new ResponseEntity(new CustomErrorType("Unable to create. A User with name " +
                    user.getName() + " already exist."), HttpStatus.CONFLICT);
        }
        User newuser = new User();
        newuser.setName(user.getName());
        newuser.setAge(user.getAge());
        newuser.setSalary(user.getSalary());
        rabbitTemplate.convertAndSend(QUEUE_USER_SAVE, newuser);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/user/{id}").buildAndExpand(user.getId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    // ------------------- Update a User ------------------------------------------------

    @RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@PathVariable("id") long id, @RequestBody User user) {
        logger.info("Updating User with id {}", id);
        UserWrapper userWrapper = (UserWrapper) rabbitTemplate.convertSendAndReceive(QUEUE_USER_FIND_BY_ID, Long.toString(id));
        User currentUser = userWrapper.getUser();
        if (currentUser == null) {
            logger.error("Unable to update. User with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Unable to upate. User with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        currentUser.setName(user.getName());
        currentUser.setAge(user.getAge());
        currentUser.setSalary(user.getSalary());
        rabbitTemplate.convertAndSend(QUEUE_USER_UPDATE, currentUser);
        return new ResponseEntity<User>(currentUser, HttpStatus.OK);
    }

    // ------------------- Delete a User-----------------------------------------

    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable("id") long id) {
        logger.info("Fetching & Deleting User with id {}", id);

        UserWrapper userWrapper = (UserWrapper) rabbitTemplate.convertSendAndReceive(QUEUE_USER_FIND_BY_ID, Long.toString(id));
        User user = userWrapper.getUser();
        if (user == null) {
            logger.error("Unable to delete. User with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Unable to delete. User with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }
        rabbitTemplate.convertAndSend(QUEUE_USER_DELETE_BY_ID, Long.toString(id));

        return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
    }
}