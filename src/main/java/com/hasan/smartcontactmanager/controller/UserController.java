package com.hasan.smartcontactmanager.controller;

import com.hasan.smartcontactmanager.helper.MyMessage;
import com.hasan.smartcontactmanager.models.Contact;
import com.hasan.smartcontactmanager.models.User;
import com.hasan.smartcontactmanager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
                                 Principal principal, HttpSession session,
                                 @RequestParam("processImage") MultipartFile multipartFile) {

        try {
            if (bindingResult.hasErrors()) {
                System.out.println("Enter");
                model.addAttribute("contact", contact);
                return "normal/add_contact_form";
            }

            String userName = principal.getName();
            User user = this.userRepository.getUserByUserName(userName);
            // processing and uploading file
            if (multipartFile.isEmpty()) {
                //
                System.out.println("File not Uploaded");
                model.addAttribute("contact", contact);
                session.setAttribute("message", new MyMessage("Please Select a Photo", "alert-danger"));
                return "normal/add_contact_form";

            } else {
                contact.setImageUrl(multipartFile.getOriginalFilename());

                // File save to any folder
                /*String userDirectory = System.getProperty("user.dir");
                String uploadDirectory = userDirectory + "\\uploadImg";
                */
                // image save to static folder
                File saveFile = new ClassPathResource("static/img/contactImage").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + multipartFile.getOriginalFilename());
                Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("File is Uploaded");
            }
            contact.setUser(user);

            user.getContacts().add(contact);
            this.userRepository.save(user);
            System.out.println("Data: " + contact);
            model.addAttribute("contact", new Contact());
            session.setAttribute("message", new MyMessage("Contact added Successfully!! ", "alert-success"));

            return "normal/add_contact_form";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("contact", contact);
            session.setAttribute("message", new MyMessage("Something went wrong " + e.getMessage(), "alert-danger"));
        }

        return "normal/add_contact_form";

    }
}
