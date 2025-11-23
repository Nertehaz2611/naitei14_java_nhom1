package org.example.foodanddrinkproject.security;


import org.example.foodanddrinkproject.exception.BadRequestException;
import org.example.foodanddrinkproject.exception.ResourceNotFoundException;
import org.example.foodanddrinkproject.enums.AuthProvider;
import org.example.foodanddrinkproject.entity.Role;
import org.example.foodanddrinkproject.entity.User;
import org.example.foodanddrinkproject.repository.RoleRepository;
import org.example.foodanddrinkproject.repository.UserRepository;
import org.example.foodanddrinkproject.security.oauth2.OAuth2UserInfo;
import org.example.foodanddrinkproject.security.oauth2.GoogleOAuth2UserInfo;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


import java.util.Collections;
import java.util.Optional;


@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {


    private final UserRepository userRepository;
    private final RoleRepository roleRepository;


    public CustomOAuth2UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }


    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);


        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new OAuth2AuthenticationException(ex.getMessage());
        }
    }


    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo;


        if (provider.equalsIgnoreCase(AuthProvider.GOOGLE.name())) {
            oAuth2UserInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());
        } else {
            throw new BadRequestException("Sorry! Login with " + provider + " is not supported yet.");
        }


        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new BadRequestException("Email not found from OAuth2 provider");
        }


        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;


        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (!user.getAuthProvider().name().equalsIgnoreCase(provider)) {
                throw new BadRequestException("Looks like you're signed up with " +
                        user.getAuthProvider() + " account. Please use your " + user.getAuthProvider() +
                        " account to login.");
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(userRequest, oAuth2UserInfo);
        }


        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }


    private User registerNewUser(OAuth2UserRequest userRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = new User();
        user.setAuthProvider(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase()));
        user.setProviderId(oAuth2UserInfo.getId());
        user.setFullName(oAuth2UserInfo.getFullName());
        user.setEmail(oAuth2UserInfo.getEmail());


        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ROLE_USER"));
        user.setRoles(Collections.singleton(userRole));


        return userRepository.save(user);
    }


    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setFullName(oAuth2UserInfo.getFullName());
        return userRepository.save(existingUser);
    }
}
