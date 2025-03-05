package com.abarquez.midterms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.abarquez.midterms.service.GoogleContactsService;

import java.util.Map;

@Controller
public class ContactsController {

    private final GoogleContactsService googleContactsService;

    @Autowired
    public ContactsController(GoogleContactsService googleContactsService) {
        this.googleContactsService = googleContactsService;
    }

    @GetMapping("/contacts")
    public String getContacts(OAuth2AuthenticationToken authToken, Model model) {
        String contacts = googleContactsService.getContacts(authToken);
        model.addAttribute("contacts", contacts);
        return "contacts/contacts"; // Ensure this matches the view path
    }

    @GetMapping("/contacts/new")
    public String showAddContactForm() {
        return "contacts/contact_form"; // Ensure this matches the view path
    }

    @PostMapping("/contacts/new")
    public String addContact(OAuth2AuthenticationToken authToken, @RequestParam Map<String, Object> contactData) {
        googleContactsService.addContact(authToken, contactData);
        return "redirect:/api/contacts";
    }

    @GetMapping("/contacts/edit/{resourceName}")
    public String showEditContactForm(@PathVariable String resourceName, Model model) {
        model.addAttribute("resourceName", resourceName);
        return "contacts/contact_form"; // Ensure this matches the view path
    }

    @PostMapping("/contacts/edit")
    public String updateContact(OAuth2AuthenticationToken authToken, @RequestParam String resourceName, @RequestParam Map<String, Object> updatedData) {
        googleContactsService.updateContact(authToken, resourceName, updatedData);
        return "redirect:/api/contacts";
    }

    @GetMapping("/contacts/delete/{resourceName}")
    public String deleteContact(OAuth2AuthenticationToken authToken, @PathVariable String resourceName) {
        googleContactsService.deleteContact(authToken, resourceName);
        return "redirect:/api/contacts";
    }
}

