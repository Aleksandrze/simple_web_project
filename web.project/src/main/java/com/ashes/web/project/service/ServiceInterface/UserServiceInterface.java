package com.ashes.web.project.service.ServiceInterface;


import com.ashes.web.project.model.User;

import java.util.List;

public interface UserServiceInterface {

    String registerUser(User user);
    String login(String username, String password);
    User getByUsername(String username);
    List<User> getAllUser();
    List<User> getAllWorkes();
    String changeUser(User user);
    String delete(User user);
    String resetPassowrd(String email);
}


