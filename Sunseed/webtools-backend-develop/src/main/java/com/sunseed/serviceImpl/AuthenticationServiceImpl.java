package com.sunseed.serviceImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.sunseed.exceptions.UnprocessableException;
import com.sunseed.helper.MailHelper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.sunseed.entity.UserProfile;
import com.sunseed.exceptions.AuthenticationException;
import com.sunseed.helper.WebClientHelper;
import com.sunseed.helper.WebClientResponseHelper;
import com.sunseed.model.requestDTO.AdminSignUpRequestDto;
import com.sunseed.model.requestDTO.ForgetPasswordRequestDto;
import com.sunseed.model.requestDTO.LoginRequestDto;
import com.sunseed.model.requestDTO.SignupRequestDto;
import com.sunseed.model.responseDTO.LoginResponseDto;
import com.sunseed.model.responseDTO.SignupResponseDto;
import com.sunseed.model.responseDTO.UserAuthResponseDto;
import com.sunseed.repository.UserProfileRepository;
import com.sunseed.service.AuthenticationService;

import lombok.RequiredArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserProfileRepository userProfileRepository;
    private final WebClientHelper webClientHelper;
    private final MessageSource messageSource;

    @Override
    public SignupResponseDto signup(SignupRequestDto request) {


        Map<String, String> authRequest = new HashMap<>();
        authRequest.put("emailId", request.getEmailId().toLowerCase());
        authRequest.put("password", request.getPassword());

        ResponseEntity<Object> authResponse = webClientHelper.signup(authRequest);

        if (authResponse.getStatusCode() == HttpStatus.CREATED) {

            JsonNode data = WebClientResponseHelper.extractDataFromResponse(authResponse.getBody());
            UserProfile newUserProfile = WebClientResponseHelper.getUserProfileForSignup(request, data);
            UserProfile savedUserProfile = userProfileRepository.save(newUserProfile);
            SignupResponseDto signupResponse = WebClientResponseHelper.getSignupResponse(savedUserProfile, data);
            return signupResponse;
        } else if (authResponse.getStatusCode() == HttpStatus.CONFLICT) {

            JsonNode data = WebClientResponseHelper.extractDataFromResponse(authResponse.getBody());
            String emailId = data.get("user").get("emailId").asText();
            Optional<UserProfile> existingUserProfile = userProfileRepository.findByEmailId(emailId);

            // distributed transaction handling to be done later
            // here just coping with error for now when user exists but user profile does
            // not exists
            if (existingUserProfile.isEmpty()) {
                UserProfile newUserProfile = WebClientResponseHelper.getUserProfileForSignup(request, data);
                UserProfile savedUserProfile = userProfileRepository.save(newUserProfile);
                SignupResponseDto signupResponse = WebClientResponseHelper.getSignupResponse(savedUserProfile, data);
                return signupResponse;

            }

            SignupResponseDto signupResponse = WebClientResponseHelper.getSignupResponse(existingUserProfile.get(),
                    data);
            throw new AuthenticationException(signupResponse, "user.exists", HttpStatus.CONFLICT);
        } else if (authResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
            throw new AuthenticationException(null, "invalid.request", HttpStatus.BAD_REQUEST);
        }
        throw new AuthenticationException(null, "internal.server.error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public SignupResponseDto adminSignup(AdminSignUpRequestDto request, String jwtToken) {
// ********** call generate random password util method *************
        String randomPassword = MailHelper.randomPasswordGenerate();


        Map<String, Object> authRequest = new HashMap<>();
        authRequest.put("emailId", request.getEmailId().toLowerCase());
        authRequest.put("password", randomPassword);
        authRequest.put("roles", request.getRoles());

        System.out.println("auth Request Map request dto :" + authRequest.get("emailId") + " "
                + authRequest.get("password") + " " + authRequest.get("roles"));

        ResponseEntity<Object> authResponse = webClientHelper.adminSignup(authRequest, jwtToken);

        if (authResponse.getStatusCode() == HttpStatus.CREATED) {

            // send mail with random password
        	StringBuilder htmlContent = new StringBuilder();
            String subject = messageSource.getMessage("password", null, LocaleContextHolder.getLocale());
//            String body = messageSource.getMessage("password.body", new Object[]{randomPassword},
//                    LocaleContextHolder.getLocale());
            htmlContent.append(String.format(messageSource.getMessage("mail.message3", null, LocaleContextHolder.getLocale()), randomPassword));

            String res = htmlContent.toString();
            MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
            formData.add("to", request.getEmailId().toLowerCase());
            formData.add("subject", "Password");
            formData.add("body", htmlContent.toString());

            String result = webClientHelper.sendMail(formData);
            if (result == null) {
                throw new UnprocessableException("password.mail.failed");
            }
//  ********** end send mail ****************

            JsonNode data = WebClientResponseHelper.extractDataFromResponse(authResponse.getBody());
            UserProfile newUserProfile = WebClientResponseHelper.getUserProfileForAdminSignup(request, data);
            UserProfile savedUserProfile = userProfileRepository.save(newUserProfile);
            SignupResponseDto signupResponse = WebClientResponseHelper.getSignupResponse(savedUserProfile, data);
            return signupResponse;
        } else if (authResponse.getStatusCode() == HttpStatus.CONFLICT) {

            JsonNode data = WebClientResponseHelper.extractDataFromResponse(authResponse.getBody());
            String emailId = data.get("user").get("emailId").asText();
            Optional<UserProfile> existingUserProfile = userProfileRepository.findByEmailId(emailId);
            if (existingUserProfile.isEmpty())
                throw new AuthenticationException(null, "user.not.found", HttpStatus.NOT_FOUND);

            SignupResponseDto signupResponse = WebClientResponseHelper.getSignupResponse(existingUserProfile.get(),
                    data);
            throw new AuthenticationException(signupResponse, "user.exists", HttpStatus.CONFLICT);
        } else if (authResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
            throw new AuthenticationException(null, "invalid.request", HttpStatus.BAD_REQUEST);
        }
        throw new AuthenticationException(null, "internal.server.error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public LoginResponseDto login(LoginRequestDto request) {

		Map<String, String> authRequest = new HashMap<>();
		authRequest.put("emailId", request.getEmailId().toLowerCase());
		authRequest.put("password", request.getPassword());
		authRequest.put("signInAs", request.getSignInAs());


        ResponseEntity<Object> authResponse = webClientHelper.login(authRequest);

        if (authResponse.getStatusCode() == HttpStatus.OK) {

            JsonNode data = WebClientResponseHelper.extractDataFromResponse(authResponse.getBody());
            String emailId = data.get("user").get("emailId").asText();
            Optional<UserProfile> existingUserProfile = userProfileRepository.findByEmailId(emailId);
            if (existingUserProfile.isEmpty())
                throw new AuthenticationException(null, "user.not.found", HttpStatus.NOT_FOUND);

            LoginResponseDto loginResponse = WebClientResponseHelper.getLoginResponse(existingUserProfile.get(), data);
            return loginResponse;
        } else if (authResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
            throw new AuthenticationException(null, "invalid.request", HttpStatus.BAD_REQUEST);
        } else if (authResponse.getStatusCode() == HttpStatus.NOT_FOUND)
            throw new AuthenticationException(null, "user.not.found", HttpStatus.NOT_FOUND);
        else if (authResponse.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY)
            throw new AuthenticationException(null, "invalid.credentials", HttpStatus.UNPROCESSABLE_ENTITY);
       else if(authResponse.getStatusCode() == HttpStatus.UNAUTHORIZED)
        throw new AuthenticationException(null, "invalid.credentials", HttpStatus.UNAUTHORIZED);
       else
            throw new AuthenticationException(null, "internal.server.error", HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public UserAuthResponseDto resetPassword(ForgetPasswordRequestDto request) {

        Optional<UserProfile> existingUserProfile = userProfileRepository
                .findByUserProfileId(request.getUserProfileId());
        if (existingUserProfile.isEmpty())
            throw new AuthenticationException(null, "user.not.found", HttpStatus.NOT_FOUND);

        Map<String, String> authRequest = new HashMap<>();
        authRequest.put("emailId", existingUserProfile.get().getEmailId().toLowerCase());
        authRequest.put("password", request.getNewPassword());

        ResponseEntity<Object> authResponse = webClientHelper.resetPassword(authRequest);

        if (authResponse.getStatusCode() == HttpStatus.OK) {
            JsonNode data = WebClientResponseHelper.extractDataFromResponse(authResponse.getBody());
            UserAuthResponseDto userResponse = WebClientResponseHelper.getUserResponse(existingUserProfile.get(), data);
            return userResponse;
        } else if (authResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
            throw new AuthenticationException(null, "invalid.request", HttpStatus.BAD_REQUEST);
        } else if (authResponse.getStatusCode() == HttpStatus.NOT_FOUND)
            throw new AuthenticationException(null, "user.not.found", HttpStatus.NOT_FOUND);

        throw new AuthenticationException(null, "internal.server.error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
