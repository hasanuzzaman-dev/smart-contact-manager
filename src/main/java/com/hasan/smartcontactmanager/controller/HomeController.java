package com.hasan.smartcontactmanager.controller;

import com.hasan.smartcontactmanager.models.Contact;
import com.hasan.smartcontactmanager.models.User;
import com.hasan.smartcontactmanager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

/*    @Autowired
    private UserRepository userRepository;

    @GetMapping("/test")
    @ResponseBody
    private String test(){

        User user  = new User();
        user.setName("Hasan");
        user.setEmail("hasan@gmail.com");
        Contact contact = new Contact();
        user.getContacts().add(contact);
        userRepository.save(user);

        return "Working";
    }*/

    @RequestMapping("/")
    private String home(Model model){
        model.addAttribute("title", "Home - Smart Contact Manager");
        return "home";
    }

    @RequestMapping("/about")
    private String about(Model model){
        model.addAttribute("title", "About - Smart Contact Manager");
        return "about";
    }

    @RequestMapping("/signUp")
    private String signUp(Model model){
        model.addAttribute("title", "SignUp - Smart Contact Manager");
        model.addAttribute("user", new User());
        return "signUp";
    }


}
