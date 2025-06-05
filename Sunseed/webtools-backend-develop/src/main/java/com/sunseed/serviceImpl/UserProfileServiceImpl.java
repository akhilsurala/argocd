package com.sunseed.serviceImpl;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.sunseed.entity.UserOtp;
import com.sunseed.enums.CommonStatus;
import com.sunseed.exceptions.AuthenticationException;
import com.sunseed.exceptions.UnprocessableException;
import com.sunseed.helper.WebClientHelper;
import com.sunseed.helper.WebClientResponseHelper;
import com.sunseed.model.requestDTO.ForgetPasswordRequestDto;
import com.sunseed.model.responseDTO.UserAuthResponseDto;
import com.sunseed.repository.UserOtpRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sunseed.entity.UserProfile;
import com.sunseed.exceptions.ResourceNotFoundException;
import com.sunseed.mappers.UserModelMapper;
import com.sunseed.model.requestDTO.UserProfileRequestDto;
import com.sunseed.model.responseDTO.UserProfileResponseDto;
import com.sunseed.repository.UserProfileRepository;
import com.sunseed.service.ImageService;
import com.sunseed.service.UserProfileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepo;
    private final WebClientHelper webClientHelper;
    private final UserOtpRepo userOtpRepo;

    private final UserModelMapper userModelMapper;

    private final ImageService imageService;

    @Value("${project.image}")
    private String path;

    @Override
    public UserProfileResponseDto updateUserProfile(UserProfileRequestDto userProfileRequestDto, long userId,
                                                    MultipartFile image) {

        System.out.println("userId :"+userId);
        UserProfile userProfile = this.userProfileRepo.findByUserId(userId)
                .orElseThrow((() -> new ResourceNotFoundException("user.not.found")));

        userProfile.setFirstName(userProfileRequestDto.getFirstName());
        userProfile.setLastName(userProfileRequestDto.getLastName());
        userProfile.setPhoneNumber(userProfileRequestDto.getPhoneNumber());

        // logic to create image and set image url
        String imagePath = userProfile.getProfilePicturePath();
        if(image!=null) {
            try {
                // imagePath is path of the image saved in the file system.
                imagePath = this.imageService.uploadImage(path, image, userProfile.getEmailId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        userProfile.setProfilePicturePath(imagePath);

        UserProfile updatedUserProfile = this.userProfileRepo.save(userProfile);
        UserProfileResponseDto userProfileResponseDto = userModelMapper
                .entityToUserProfileResponseDto(updatedUserProfile);

        return userProfileResponseDto;

    }

    @Override
    public UserProfileResponseDto getUserDetailsResponse(Long userId) {
        UserProfile userProfile = this.userProfileRepo.findByUserId(userId)
                .orElseThrow((() -> new ResourceNotFoundException("user.not.found")));

        UserProfileResponseDto userProfileResponseDto = UserProfileResponseDto.builder().userProfileId(userProfile.getUserProfileId()).profilePicturePath(userProfile.getProfilePicturePath()).firstName(userProfile.getFirstName()).lastName(userProfile.getLastName()).phoneNumber(userProfile.getPhoneNumber()).build();
        return userProfileResponseDto;
    }


    @Override
    public UserAuthResponseDto changePassword(ForgetPasswordRequestDto request, Long userId) {
        UserProfile userProfile = this.userProfileRepo.findByUserId(userId)
                .orElseThrow((() -> new ResourceNotFoundException("user.not.found")));

        // check otp is  valid or not
        Integer otp = request.getOtp();
        if (otp == null)
            throw new ResourceNotFoundException("otp.not.found");

        List<UserOtp> userOtps = userOtpRepo.findByUserProfileId(userProfile.getUserProfileId());


        if (!userOtps.isEmpty()) {

            System.out.println(userOtps.get(0).getOtp());
            System.out.println(userOtps.get(0).getOtpStatus());
            UserOtp lastUserOtp = userOtps.get(userOtps.size() - 1);
            Integer dbOtp = lastUserOtp.getOtp();
            Instant creationInstant = lastUserOtp.getCreatedAt();
            LocalDateTime creationTime = LocalDateTime.ofInstant(creationInstant, ZoneId.systemDefault());

            if (dbOtp.equals(otp)) {
                LocalDateTime currentTime = LocalDateTime.now();
                Duration duration = Duration.between(creationTime, currentTime);
                Long minute = duration.toMinutes();
                System.out.println(minute);
                if (minute > 60 || lastUserOtp.getOtpStatus().equals(CommonStatus.INACTIVE)) {
                    System.out.println("otp expired");

                    lastUserOtp.setOtpStatus(CommonStatus.INACTIVE);

                    throw new UnprocessableException("error.invalidotp");

                }
            } else {
                throw new UnprocessableException("error.invalidotp");
            }
            lastUserOtp.setOtpStatus(CommonStatus.INACTIVE);
            userOtpRepo.save(lastUserOtp);


            // reset password
            Map<String, String> authRequest = new HashMap<>();
            authRequest.put("emailId", userProfile.getEmailId());
            authRequest.put("password", request.getNewPassword());

            ResponseEntity<Object> authResponse = webClientHelper.resetPassword(authRequest);

            if (authResponse.getStatusCode() == HttpStatus.OK) {

                JsonNode data = WebClientResponseHelper.extractDataFromResponse(authResponse.getBody());
                UserAuthResponseDto userResponse = WebClientResponseHelper.getUserResponse(userProfile, data);
                return userResponse;
            } else if (authResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new AuthenticationException(null, "invalid.request", HttpStatus.BAD_REQUEST);
            } else if (authResponse.getStatusCode() == HttpStatus.NOT_FOUND)
                throw new AuthenticationException(null, "user.not.found", HttpStatus.NOT_FOUND);

            throw new AuthenticationException(null, "internal.server.error", HttpStatus.INTERNAL_SERVER_ERROR);


        } else {
            throw new ResourceNotFoundException("otp.not.found");

        }
    }
}
