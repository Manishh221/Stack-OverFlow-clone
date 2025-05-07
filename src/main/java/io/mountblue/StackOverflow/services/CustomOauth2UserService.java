package io.mountblue.StackOverflow.services;
import io.mountblue.StackOverflow.entity.Users;
import io.mountblue.StackOverflow.repositories.UserRepository;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class CustomOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepo;

    public CustomOauth2UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        try {
            OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
            OAuth2User oauthUser = delegate.loadUser(request);

            System.out.println("OAuth2 attributes: " + oauthUser.getAttributes());

            String email = extractEmail(oauthUser);
            if (email == null) {
                throw new OAuth2AuthenticationException("Email not found in OAuth2 response");
            }

            String name = extractName(oauthUser);
            System.out.println("Extracted email: " + email + ", name: " + name);

            Users user = userRepo.findByEmail(email);
            if (user == null) {
                user = new Users();
                user.setEmail(email);
                user.setUsername(name != null ? name : email.split("@")[0]);
                user.setReputation(0);
                // Your User entity requires a non-null password, so set a secure random one
                user.setPassword(generateRandomPassword());
                user.setRole("USER");
                userRepo.save(user);
                System.out.println("Created new user with email: " + email);
            } else {
                System.out.println("Found existing user: " + user.getUsername());
            }

            return new DefaultOAuth2User(
                    AuthorityUtils.createAuthorityList(user.getRole()),
                    oauthUser.getAttributes(),
                    "email"
            );
        } catch (Exception e) {
            System.err.println("Error in OAuth2 processing: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private String extractEmail(OAuth2User oauthUser) {
        // Direct attribute
        if (oauthUser.getAttribute("email") != null) {
            return oauthUser.getAttribute("email");
        }

        // Nested in 'attributes' object
        Map<String, Object> attributes = oauthUser.getAttributes();
        if (attributes.containsKey("attributes") && attributes.get("attributes") instanceof Map) {
            Map<String, Object> nestedAttrs = (Map<String, Object>) attributes.get("attributes");
            if (nestedAttrs.containsKey("email")) {
                return (String) nestedAttrs.get("email");
            }
        }

        return null;
    }

    private String extractName(OAuth2User oauthUser) {

        if (oauthUser.getAttribute("name") != null) {
            return oauthUser.getAttribute("name");
        }

        // Fallback to given_name + family_name
        String firstName = oauthUser.getAttribute("given_name");
        String lastName = oauthUser.getAttribute("family_name");

        if (firstName != null) {
            return firstName + (lastName != null ? " " + lastName : "");
        }

        return null;
    }

    private String generateRandomPassword() {
        // Generate a secure random password since your User entity requires a non-null password
        return new BCryptPasswordEncoder().encode(UUID.randomUUID().toString());
    }
}