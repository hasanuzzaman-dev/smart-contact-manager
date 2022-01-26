package com.hasan.smartcontactmanager.controller;

import com.hasan.smartcontactmanager.helper.MyMessage;
import com.hasan.smartcontactmanager.models.Contact;
import com.hasan.smartcontactmanager.models.User;
import com.hasan.smartcontactmanager.repositories.ContactRepository;
import com.hasan.smartcontactmanager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
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
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;


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
    public String processContact(@Valid @ModelAttribute Contact contact, @RequestParam("processImage") MultipartFile multipartFile, BindingResult bindingResult,
                                 Model model, Principal principal, HttpSession session) {

        try {
            if (bindingResult.hasErrors()) {
                model.addAttribute("contact", contact);
                return "normal/add_contact_form";
            }

            String userName = principal.getName();
            User user = this.userRepository.getUserByUserName(userName);

            // You can use throw exception on alert box
            /*if (3>2){
                throw new  Exception();
                // goto catch block
            }*/

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

            /*Message Success*/
            session.setAttribute("message", new MyMessage("Contact added Successfully!! ", "alert-success"));

            return "normal/add_contact_form";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("contact", contact);
            /* Message Success */
            session.setAttribute("message", new MyMessage("Something went wrong " + e.getMessage(), "alert-danger"));
        }

        return "normal/add_contact_form";

    }

    // Show Contact handler
    // For pagination you need per page contact [n=5] and current page = [page = 0]
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {

        model.addAttribute("title", "Show user contacts");

        // Get signed user
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);
        Pageable pageable = PageRequest.of(page, 2);
        Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);

        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", contacts.getTotalPages());

        return "normal/show_contacts";

    }

    // Showing particular contact details
    @RequestMapping("/{cid}/contact")
    public String showContactDetails(@PathVariable("cid") Integer cid, Model model, Principal principal) {
        // System.out.println("Cid:"+cid);
        model.addAttribute("title", "Contact");

        Optional<Contact> contactOptional = this.contactRepository.findById(cid);


        if (contactOptional.isPresent()) {
            Contact contact = contactOptional.get();

            // get current user
            String username = principal.getName();
            User user = this.userRepository.getUserByUserName(username);

            // show contact only current user
            if (user.getId() == contact.getUser().getId()) {
                model.addAttribute("contact", contact);
            }
        }

        return "normal/contact_details";
    }

    // delete contact handler
    @GetMapping("/delete/{cid}")
    public String deleteContact(@PathVariable("cid") Integer cid, Model model, Principal principal, HttpSession session) {
        Optional<Contact> contactOptional = this.contactRepository.findById(cid);
        if (contactOptional.isPresent()) {
            Contact contact = contactOptional.get();

            // get current user
            String username = principal.getName();
            User user = this.userRepository.getUserByUserName(username);

            // delete contact only current user
            if (user.getId() == contact.getUser().getId()) {
                this.contactRepository.delete(contact);

                session.setAttribute("message", new MyMessage("Contact deleted Successfully", "alert-success"));
            }

        }
        return "redirect:/user/show-contacts/0";
    }

    // Open update from handler
    @PostMapping("/update-contact/{cid}")
    public String openUpdateForm(@PathVariable("cid") Integer cid, Model model) {

        model.addAttribute("title", "Update Contact");
        Contact contact = this.contactRepository.findById(cid).get();
        model.addAttribute("contact", contact);
        return "normal/update_form";
    }

    // update Contact handler
    @RequestMapping(value = "/process-update", method = RequestMethod.POST)
    public String updateForm(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile multipartFile,
                             Model model, Principal principal, HttpSession session) {
        try {
            // Fetch old contact
            Contact oldContact = this.contactRepository.findById((contact.getcId())).get();
            if (!multipartFile.isEmpty()) {
                // file rewrite
                // At first delete old photo and update photo
                // delete photo
                File deleteFile = new ClassPathResource("static/img/contactImage").getFile();
                File oldFile = new File(deleteFile, oldContact.getImageUrl());
                boolean isDelete = oldFile.delete();

                // Update photo
                File saveFile = new ClassPathResource("static/img/contactImage").getFile();
                // rename file with currentTimeMillis
                String filename = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                filename = System.currentTimeMillis() + filename.toLowerCase().replaceAll(" ", "-");
                Path rootLocation = Paths.get(saveFile + File.separator);

                Files.copy(multipartFile.getInputStream(), rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);

                contact.setImageUrl(filename);

            } else {
                contact.setImageUrl(oldContact.getImageUrl());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        User user = this.userRepository.getUserByUserName(principal.getName());
        contact.setUser(user);
        this.contactRepository.save(contact);

        session.setAttribute("message", new MyMessage("Your contact is updated...", "alert-success"));

        // redirect uses for URL not html file
        return "redirect:/user/" + contact.getcId() + "/contact";
    }

    // Profile handler
    @GetMapping("/profile")
    public String yourProfile(Model model) {

        model.addAttribute("title", "Profile");

        return "normal/profile";
    }


    // open setting handler
    @GetMapping("/settings")
    public String openSetting(Model model) {
        model.addAttribute("title", "Settings");
        return "normal/settings";
    }

    // change password handler
    @PostMapping("/change-password")
    public String changePassword(Model model, Principal principal, HttpSession session,
                                 @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) {

        String username = principal.getName();
        User currentUser = this.userRepository.getUserByUserName(username);

        if (this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
            currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
            this.userRepository.save(currentUser);
            session.setAttribute("message", new MyMessage("Your password is successfully changed...", "alert-success"));

        } else {
            session.setAttribute("message", new MyMessage("Your old password is wrong!!", "alert-danger"));
            return "redirect:/user/settings";
        }

        return "redirect:/user/index";

    }

}
