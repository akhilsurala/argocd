package com.sunseed.serviceImpl;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunseed.entity.UserOtp;
import com.sunseed.entity.UserProfile;
import com.sunseed.enums.CommonStatus;
import com.sunseed.enums.OtpType;
import com.sunseed.exceptions.MailException;
import com.sunseed.helper.MailHelper;
import com.sunseed.helper.WebClientHelper;
import com.sunseed.helper.WebClientResponseHelper;
import com.sunseed.model.responseDTO.LoginResponseDto;
import com.sunseed.model.responseDTO.UserAuthResponseDto;
import com.sunseed.repository.UserOtpRepo;
import com.sunseed.repository.UserProfileRepository;
import com.sunseed.service.MailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

	private final MessageSource messageSource;

	private final UserOtpRepo userOtpRepo;

	private final UserProfileRepository userProfileRepo;
	private final WebClientHelper webClientHelper;
	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public UserAuthResponseDto sendOTP(String email, String otpFor) {
		Map<String, String> userMap = new HashMap<>();
		userMap.put("emailId", email);

		Long userId = null;
		Boolean isVerified = null;
		Set<String> roles = null;
		
		ResponseEntity<Object> authResponse = webClientHelper.getUser(userMap);
		if (authResponse.getStatusCode() == HttpStatus.OK) {

			// get Data from user
			JsonNode dataNode = WebClientResponseHelper.extractDataFromResponse(authResponse.getBody());
			JsonNode userIdNode = dataNode.get("userId");
			JsonNode isVerifiedNode = dataNode.get("isVerified");

			if (!(userIdNode.isNull())) {
				userId = userIdNode.asLong();
				isVerified = isVerifiedNode.asBoolean();
				Object rolesObject = dataNode.get("roles");
				List<String> rolesList = objectMapper.convertValue(rolesObject, new TypeReference<List<String>>() {
				});
				roles = new HashSet<>(rolesList);
				log.info("User ID: {}", userId);
			} else {
				log.warn("User ID not found in data.");
				throw new MailException(null,"user.id.not.found",HttpStatus.NOT_FOUND);
			}
		}
		else if(authResponse.getStatusCode()==HttpStatus.BAD_REQUEST)
			throw new MailException(null,"field.required",HttpStatus.BAD_REQUEST);
		else if(authResponse.getStatusCode()==HttpStatus.NOT_FOUND)
			throw new MailException(null,"user.not.found",HttpStatus.NOT_FOUND);
		else
			throw new MailException(null, "internal.server.error", HttpStatus.INTERNAL_SERVER_ERROR);

		// get user profile
		UserProfile userProfile = userProfileRepo.findByUserId(userId)
				.orElseThrow(() -> new MailException(null,"user.not.found",HttpStatus.NOT_FOUND));
		List<UserOtp> userOtps = userOtpRepo.findByUserProfileId(userProfile.getUserProfileId());
		if (!userOtps.isEmpty()) {
			UserOtp lastUserOtp = userOtps.get(userOtps.size() - 1);
			lastUserOtp.setOtpStatus(CommonStatus.INACTIVE);
			userOtpRepo.save(lastUserOtp);
		}

		int otpGenerated = MailHelper.otpGeneration();
		log.info("otp is : {}", otpGenerated);

		// ------ mail content -----------
		StringBuilder htmlContent = new StringBuilder();
//		htmlContent.append(messageSource.getMessage("mail.message1", null, LocaleContextHolder.getLocale()))
//				.append(otpGenerated).append("</h4>")
//				.append(messageSource.getMessage("mail.message2", null, LocaleContextHolder.getLocale()));

		String subject;
		if (OtpType.EMAIL_VERIFICATION.getValue().equalsIgnoreCase(otpFor)) {

			subject = messageSource.getMessage("mail.subject1", null, LocaleContextHolder.getLocale());
			htmlContent.append(String.format(messageSource.getMessage("mail.message1", null, LocaleContextHolder.getLocale()), otpGenerated));
		} else if(OtpType.FORGET_PASSWORD.getValue().equalsIgnoreCase(otpFor)) {

			subject = messageSource.getMessage("mail.subject2", null, LocaleContextHolder.getLocale());
			htmlContent.append(String.format(messageSource.getMessage("mail.message2", null, LocaleContextHolder.getLocale()), otpGenerated));
		}
		else{
			subject = messageSource.getMessage("mail.subject3", null, LocaleContextHolder.getLocale());
			htmlContent.append(String.format(messageSource.getMessage("mail.message2", null, LocaleContextHolder.getLocale()), otpGenerated));
		}

		// ------ send mail with otp -- request to mail service---------

		MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
		formData.add("to", email);
		formData.add("subject", subject);
		formData.add("body", htmlContent.toString());
		// formData.add("cc", Arrays.asList("cc1@example.com", "cc2@example.com"));
		// formData.add("file", file);

		// sending mail from microservice
		String result = webClientHelper.sendMail(formData);

		log.info("mail service response : {}", result);

		// ----- otp save in db ------------
		UserOtp userOtp = UserOtp.builder().otp(otpGenerated).otpStatus(CommonStatus.ACTIVE)
				.userProfileId(userProfile.getUserProfileId()).build();
		userOtpRepo.save(userOtp);
		UserAuthResponseDto userAuthResponseDto = UserAuthResponseDto.builder()
				.userProfileId(userProfile.getUserProfileId()).emailId(userProfile.getEmailId())
				.firstName(userProfile.getFirstName()).lastName(userProfile.getLastName())
				.phoneNumber(userProfile.getPhoneNumber()).isVerified(isVerified).roles(roles).build();
		return userAuthResponseDto;
	}

	@Override
	public LoginResponseDto verifyEmailUsingOtp(Integer otp, String email, String otpFor) {

		UserProfile userProfile = userProfileRepo.findByEmailId(email)
				.orElseThrow((() -> new MailException(null,"user.not.found",HttpStatus.NOT_FOUND)));
		log.info("email is : {}", email);

		List<UserOtp> userOtps = userOtpRepo.findByUserProfileId(userProfile.getUserProfileId());
		System.out.println(userOtps.get(0).getOtp());
		System.out.println(userOtps.get(0).getOtpStatus());

		if (!userOtps.isEmpty()) {
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
					throw new MailException(null,"error.invalidotp",HttpStatus.UNPROCESSABLE_ENTITY);

				}
			} else {
				throw new MailException(null,"error.invalidotp",HttpStatus.UNPROCESSABLE_ENTITY);
			}

			lastUserOtp.setOtpStatus(CommonStatus.INACTIVE);
			userOtpRepo.save(lastUserOtp);
			LoginResponseDto loginRes = new LoginResponseDto();
			UserAuthResponseDto user = UserAuthResponseDto.builder().userProfileId(userProfile.getUserProfileId())
					.emailId(email).firstName(userProfile.getFirstName()).lastName(userProfile.getLastName())
					.phoneNumber(userProfile.getPhoneNumber()).build();
			Set<String> roles = null;

			if (OtpType.EMAIL_VERIFICATION.getValue().equalsIgnoreCase(otpFor)) {

				Map<String, String> userMap = new HashMap<>();
				userMap.put("emailId", email);
				Boolean isVerified = null;
				String jwtToken = null;
				
				ResponseEntity<Object> authResponse = webClientHelper.verifyUser(userMap);
				
				if(authResponse.getStatusCode()==HttpStatus.OK) {
					
					JsonNode dataNode = WebClientResponseHelper.extractDataFromResponse(authResponse.getBody());
					JsonNode accessTokenNode = dataNode.get("accessToken");
					isVerified = dataNode.get("user").get("isVerified").asBoolean();
					jwtToken = accessTokenNode.asText();
					Object rolesObject = dataNode.get("user").get("roles");
					List<String> rolesList = objectMapper.convertValue(rolesObject, new TypeReference<List<String>>() {
					});
					roles = new HashSet<>(rolesList);
				}
				else if(authResponse.getStatusCode()==HttpStatus.BAD_REQUEST)
					throw new MailException(null,"field.required",HttpStatus.BAD_REQUEST);
				else if(authResponse.getStatusCode()==HttpStatus.NOT_FOUND)
					throw new MailException(null,"user.not.found",HttpStatus.NOT_FOUND);
				else
					throw new MailException(null,"internal.server.error",HttpStatus.INTERNAL_SERVER_ERROR);

				log.info("data node : {}", jwtToken);
				user.setVerified(isVerified);
				user.setRoles(roles);
				loginRes.setUser(user);
				loginRes.setAccessToken(jwtToken);
				return loginRes;

			} else {
				user.setRoles(roles);
				loginRes.setUser(user);
				return loginRes;
			}

		} else {
			throw new MailException(null,"otp.not.found",HttpStatus.NOT_FOUND);
		}

	}
}
