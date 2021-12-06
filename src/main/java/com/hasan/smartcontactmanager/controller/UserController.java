package com.hasan.smartcontactmanager.controller;

import com.hasan.smartcontactmanager.helper.MyMessage;
import com.hasan.smartcontactmanager.models.Contact;
import com.hasan.smartcontactmanager.models.User;
import com.hasan.smartcontactmanager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // This method run for every method index, add_contact or etc.
    // Method for adding common data for response.
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        String userName = principal.getName();
        User user = userRepository.getUserByUserName(userName);
        model.addAttribute("user", user);
    }

    @RequestMapping("/index")
    public String dashBoard(Model model, Principal principal) {
        model.addAttribute("title", "User Dashboard");
        return "normal/user_dashboard";
    }

    // Open add form handler
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }

    //Processing add contact form
    @PostMapping("/process-contact")
    public String processContact(@Valid @ModelAttribute Contact contact,
                                 BindingResult bindingResult, Model model,
                                 Principal principal, HttpSession session) {

        try {
            if (bindingResult.hasErrors()){
                model.addAttribute("contact",contact);
                return "normal/add_contact_form";
            }

            String userName = principal.getName();
            User user = this.userRepository.getUserByUserName(userName);
            contact.setUser(user);

            user.getContacts().add(contact);
            this.userRepository.save(user);
            System.out.println("Data: " + contact);

            System.out.println("Added to Database");
            session.setAttribute("message",new MyMessage("Contact added Successfully!! ", "alert-success"));

            return "normal/add_contact_form";
        }catch (Exception e){
            e.printStackTrace();
            model.addAttribute("contact",contact);
            session.setAttribute("message", new MyMessage("Something went wrong "+e.getMessage(),"alert-danger"));
        }

        return "normal/add_contact_form";

    }
}
