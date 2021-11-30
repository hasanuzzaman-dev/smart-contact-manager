package com.hasan.smartcontactmanager.controller;

import com.hasan.smartcontactmanager.models.User;
import com.hasan.smartcontactmanager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/index")
    public String dashBoard(Model model, Principal principal){
        String userName = principal.getName();
        System.out.println("USERNAME: "+userName);

        // get the username using username
        User user = userRepository.getUserByUserName(userName);
        System.out.println("USER: "+user.toString());
        model.addAttribute("user", user);

        return "normal/user_dashboard";
    }
}
