package com.hasan.smartcontactmanager.controller;

import com.hasan.smartcontactmanager.helper.MyMessage;
import com.hasan.smartcontactmanager.models.User;
import com.hasan.smartcontactmanager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class SignUpController {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    // Handler for registering user
    @RequestMapping(value = "/do_register", method = RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult bindingResult,
                               @RequestParam(value = "agreement", defaultValue = "false") Boolean agreement,
                               Model model,
                               HttpSession session) {

        try {

            if (!agreement) {
                System.out.println("You have not agreed the terms & conditions");
                throw new Exception("You have not agreed the terms & conditions");
            }
            if (bindingResult.hasErrors()){
                model.addAttribute("user", user);
                return "signUp";
            }
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            System.out.println("USER: " + user.toString());
            System.out.println("AGREEMENT: " + agreement);

            User savedUser = this.userRepository.save(user);

            // Show empty user in frontend
            model.addAttribute("user", new User());

            session.setAttribute("message",new MyMessage("Successfully Registered!! ", "alert-success"));

            return "signUp";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute(
                    "message",
                    new MyMessage("Something went wrong !! " + e.getMessage(), "alert-danger")
            );
            return "signUp";
        }


    }
}
