package com.midterms.labactivity.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GoogleContactsService {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final RestTemplate restTemplate;

    @Autowired
    public GoogleContactsService(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
        this.restTemplate = new RestTemplate();
    }

    private String getAccessToken(OAuth2AuthenticationToken authToken) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
            authToken.getAuthorizedClientRegistrationId(), authToken.getName());
        return client.getAccessToken().getTokenValue();
    }

    public String getContacts(OAuth2AuthenticationToken authToken) {
        String accessToken = getAccessToken(authToken);
        String apiUrl = "https://people.googleapis.com/v1/people/me/connections?personFields=names,emailAddresses";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }

    public String addContact(OAuth2AuthenticationToken authToken, Map<String, Object> contactData) {
        String accessToken = getAccessToken(authToken);
        String apiUrl = "https://people.googleapis.com/v1/people:createContact";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(contactData, headers);

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
        return response.getBody();
    }

    public String updateContact(OAuth2AuthenticationToken authToken, String resourceName, Map<String, Object> updatedData) {
        String accessToken = getAccessToken(authToken);
        String updateMask = "names,emailAddresses";
        String apiUrl = "https://people.googleapis.com/v1/" + resourceName + ":updateContact?updatePersonFields=" + updateMask;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(updatedData, headers);

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.PATCH, entity, String.class);
        return response.getBody();
    }

    public String deleteContact(OAuth2AuthenticationToken authToken, String resourceName) {
        String accessToken = getAccessToken(authToken);
        String apiUrl = "https://people.googleapis.com/v1/" + resourceName + ":deleteContact";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.DELETE, entity, String.class);
        return response.getBody();
    }
}
