package com.midterms.labactivity.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.midterms.labactivity.service.GoogleContactsService;

@Controller
@RequestMapping("/contacts")
public class ContactsController {

    private final GoogleContactsService googleContactsService;

    public ContactsController(GoogleContactsService googleContactsService) {
        this.googleContactsService = googleContactsService;
    }

    @GetMapping
    public String listContacts(Model model, OAuth2AuthenticationToken authToken) {
        String contactsJson = googleContactsService.getContacts(authToken);
        model.addAttribute("contacts", contactsJson);
        return "contacts"; 
    }

    @GetMapping("/new")
    public String newContactForm(Model model) {
        model.addAttribute("contact", new HashMap<String, Object>());
        return "contact_form"; 
    }

    @PostMapping("/new")
    public String addContact(@RequestParam Map<String, String> formData, OAuth2AuthenticationToken authToken) {
        Map<String, Object> contactData = new HashMap<>();
        Map<String, Object> name = new HashMap<>();
        name.put("givenName", formData.get("givenName"));
        name.put("familyName", formData.get("familyName"));
        contactData.put("names", Collections.singletonList(name));
        
        if (formData.containsKey("email") && !formData.get("email").isEmpty()) {
            Map<String, Object> email = new HashMap<>();
            email.put("value", formData.get("email"));
            contactData.put("emailAddresses", Collections.singletonList(email));
        }
        
        googleContactsService.addContact(authToken, contactData);
        return "redirect:/contacts";
    }

    @GetMapping("/edit")
    public String editContactForm(@RequestParam("resourceName") String resourceName, Model model) {
        model.addAttribute("resourceName", resourceName);
        return "contact_form"; 
    }

    @PostMapping("/edit")
    public String updateContact(@RequestParam("resourceName") String resourceName,
                                @RequestParam Map<String, String> formData,
                                OAuth2AuthenticationToken authToken) {
        Map<String, Object> updatedData = new HashMap<>();
        Map<String, Object> name = new HashMap<>();
        name.put("givenName", formData.get("givenName"));
        name.put("familyName", formData.get("familyName"));
        updatedData.put("names", Collections.singletonList(name));

        if (formData.containsKey("email") && !formData.get("email").isEmpty()) {
            Map<String, Object> email = new HashMap<>();
            email.put("value", formData.get("email"));
            updatedData.put("emailAddresses", Collections.singletonList(email));
        }
        
        googleContactsService.updateContact(authToken, resourceName, updatedData);
        return "redirect:/contacts";
    }

    @GetMapping("/delete")
    public String deleteContact(@RequestParam("resourceName") String resourceName, OAuth2AuthenticationToken authToken) {
        googleContactsService.deleteContact(authToken, resourceName);
        return "redirect:/contacts";
    }
}
