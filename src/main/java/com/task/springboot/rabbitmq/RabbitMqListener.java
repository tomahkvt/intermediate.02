package com.task.springboot.rabbitmq;

import com.task.springboot.model.User;
import com.task.springboot.model.UserWrapper;
import com.task.springboot.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.task.springboot.config.RabbitConfiguration.*;

@Component
public class RabbitMqListener {
    @Autowired
    UserService userService;

    Logger logger = Logger.getLogger(RabbitMqListener.class);


    @RabbitListener(queues = QUEUE_GET_ALL_USERS)
    public List<User> GetAllUsers(String message) throws InterruptedException {
        logger.info("Received on worker GetAllUsersRequest");
        return userService.findAllUsers();
    }

    @RabbitListener(queues = QUEUE_USER_FIND_BY_ID)
    public UserWrapper queueUserFindById(String id) throws InterruptedException {
        logger.info("Received on worker queueUserFindById");
        Long longid = Long.parseLong(id);
        return new UserWrapper(userService.findById(longid));
    }

    @RabbitListener(queues = QUEUE_USER_FIND_BY_NAME)
    public UserWrapper queueUserFindByName(String name) throws InterruptedException {
        logger.info("Received on worker queueUserFindByName");
        User user = userService.findByName(name);
        return new UserWrapper(user);
    }

    @RabbitListener(queues = QUEUE_USER_SAVE)
    public void queueUserSave(User user) throws InterruptedException {
        logger.info("Received on worker queueUserSave");
        userService.saveUser(user);
    }

    @RabbitListener(queues = QUEUE_USER_UPDATE)
    public void queueUserUpdate(User user) throws InterruptedException {
        logger.info("Received on worker queueUserUpdate");
        userService.updateUser(user);
    }

    @RabbitListener(queues = QUEUE_USER_DELETE_BY_ID)
    public void queueUserDeleteById(String id) throws InterruptedException {
        logger.info("Received on worker queueUserDeleteById");
        Long longid = Long.parseLong(id);
        userService.deleteUserById(longid);
    }

}