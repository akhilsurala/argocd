package com.sunseed.serviceImpl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunseed.entity.UserProfile;
import com.sunseed.exceptions.InvalidDataException;
import com.sunseed.exceptions.ResourceNotFoundException;
import com.sunseed.exceptions.UnAuthorizedException;
import com.sunseed.exceptions.UnprocessableException;
import com.sunseed.mappers.UserModelMapper;
import com.sunseed.model.requestDTO.BlockUserRequestDto;
import com.sunseed.model.requestDTO.UserRequestDto;
import com.sunseed.model.responseDTO.UserResponseDto;
import com.sunseed.repository.UserProfileRepository;
import com.sunseed.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements UserService {

	private final UserProfileRepository userProfileRepo;
	private final UserModelMapper userModelMapper;
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Value("${auth.url}")
    private String authorisationUrl;
	
	@Autowired
	private WebClient.Builder webClientBuilder;

	@Override
	public UserResponseDto getUser(Long userId, String jwtToken) {

//		String authUrl = "http://localhost:8081/auth/v1/admin/users/{userId}";
		String authUrl = authorisationUrl + "/auth/v1/admin/users/{userId}";
		UserResponseDto responseDto = new UserResponseDto();
		UserProfile userProfile = this.userProfileRepo.findByUserId(userId)
				.orElseThrow((() -> new ResourceNotFoundException("user.not.found")));

		try {

			WebClient webClient = webClientBuilder.build();
			Mono<String> resultMono = webClient.get().uri(authUrl, userId).header(HttpHeaders.AUTHORIZATION, jwtToken).retrieve()
					.bodyToMono(String.class);

			String result = resultMono.block();

			JsonNode rootNode = objectMapper.readTree(result);

			JsonNode dataNode = rootNode.get("data");

			long tempUserId = dataNode.get("userId").asLong();
			boolean isActive = dataNode.get("isActive").asBoolean();
			boolean isVerified = dataNode.get("isVerified").asBoolean();

			JsonNode rolesNode = dataNode.get("roles");
			
			String createdAt = dataNode.get("createdAt").asText();
            String updatedAt = dataNode.get("updatedAt").asText();

			Set<String> roles = new HashSet<>();
			if (rolesNode.isArray()) {
				for (JsonNode role : rolesNode) {
					roles.add(role.asText());
				}
			}

			responseDto = this.userModelMapper.authServiceToResponseDto(userProfile, tempUserId, isActive, isVerified,
					roles, createdAt, updatedAt);

		} catch (WebClientResponseException ex) {
			if (ex.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY)
				throw new UnprocessableException("deleted.user.cannot.perform.this.operation");

			if (ex.getStatusCode() == HttpStatus.NOT_FOUND)
				throw new ResourceNotFoundException("user.not.found");
		}

		catch (JsonProcessingException e) {

			throw new InvalidDataException("error.from.jsonprocessing");
		}

		return responseDto;
	}

	@Override
	public List<UserResponseDto> getAllUsers(String jwtToken, String search) {

		String authUrl;
		if(search != null) {
			String encodedString = URLEncoder.encode(search, StandardCharsets.UTF_8);
//			authUrl = "http://localhost:8081/auth/v1/admin/users?search=" + encodedString;
			authUrl = authorisationUrl + "/auth/v1/admin/users?search=" + encodedString;
		}
		else {
//			authUrl = "http://localhost:8081/auth/v1/admin/users";
			authUrl = authorisationUrl + "/auth/v1/admin/users";
		}
//		String authUrl = "http://localhost:8081/auth/v1/admin/users";
		List<UserResponseDto> responseDtoList = new ArrayList<>();
		UserProfile userProfile = new UserProfile();

		try {
			WebClient webClient = webClientBuilder.build();
			Mono<String> resultMono = webClient.get().uri(authUrl).header(HttpHeaders.AUTHORIZATION, jwtToken).retrieve()
					.bodyToMono(String.class);

			String result = resultMono.block();

			JsonNode rootNode = objectMapper.readTree(result);

			JsonNode dataArray = rootNode.get("data");
			for (JsonNode element : dataArray) {

				long userId = element.get("userId").asLong();
				boolean isVerified = element.get("isVerified").asBoolean();
				boolean isActive = element.get("isActive").asBoolean();

				JsonNode rolesNode = element.get("roles");

				Set<String> roles = new HashSet<>();
				if (rolesNode.isArray()) {
					for (JsonNode role : rolesNode) {
						roles.add(role.asText());
					}
				}
				
				String createdAt = element.get("createdAt").asText();
	            String updatedAt = element.get("updatedAt").asText();

				userProfile = this.userProfileRepo.findByUserId(userId)
						.orElseThrow((() -> new ResourceNotFoundException("user.not.found")));

				UserResponseDto responseDto = this.userModelMapper.authServiceToResponseDto(userProfile, userId,
						isActive, isVerified, roles, createdAt, updatedAt);

				responseDtoList.add(responseDto);

			}

		}

		catch (WebClientResponseException ex) {
			ex.printStackTrace();
			if (ex.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY)
				throw new UnprocessableException("deleted.user.cannot.perform.this.operation");

			if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED)
				throw new UnAuthorizedException("user.not.authorized");
		} catch (JsonProcessingException e) {

			throw new InvalidDataException("error.from.jsonprocessing");
		}

		return responseDtoList;
	}

	@Transactional
	@Override
	public UserResponseDto updateUser(Long userId, UserRequestDto requestDto, String jwtToken) {

		UserResponseDto responseDto = new UserResponseDto();
		Map<String, Object> authRequest = new HashMap<>();

		UserProfile userProfile = this.userProfileRepo.findByUserId(userId)
				.orElseThrow((() -> new ResourceNotFoundException("user.not.found")));

		authRequest.put("emailId", requestDto.getEmailId());
		authRequest.put("roles", requestDto.getRoles());

		try {

			WebClient webClient = webClientBuilder.build();
			Mono<String> responseMono = webClient.put()
//					.uri("http://localhost:8081/auth/v1/admin/users/{targetUserId}", userId)
					.uri(authorisationUrl + "/auth/v1/admin/users/{targetUserId}", userId)
					.header(HttpHeaders.AUTHORIZATION, jwtToken).body(BodyInserters.fromValue(authRequest)).retrieve()
					.bodyToMono(String.class);

			String result = responseMono.block();

			JsonNode rootNode = objectMapper.readTree(result);

			JsonNode dataNode = rootNode.get("data");

			long tempUserId = dataNode.get("userId").asLong();
			boolean isVerified = dataNode.get("isVerified").asBoolean();
			boolean isActive = dataNode.get("isActive").asBoolean();

			JsonNode rolesNode = dataNode.get("roles");
			
			String createdAt = dataNode.get("createdAt").asText();
            String updatedAt = dataNode.get("updatedAt").asText();

			Set<String> roles = new HashSet<>();
			if (rolesNode.isArray()) {
				for (JsonNode role : rolesNode) {
					roles.add(role.asText());
				}
			}

			if (!isVerified) {
				userProfile.setEmailId(requestDto.getEmailId());
			}

			userProfile.setFirstName(requestDto.getFirstName());
			userProfile.setLastName(requestDto.getLastName());
			userProfile.setPhoneNumber(requestDto.getPhoneNumber());

			userProfile = this.userProfileRepo.save(userProfile);

			responseDto = this.userModelMapper.authServiceToResponseDto(userProfile, tempUserId, isActive, isVerified,
					roles, createdAt, updatedAt);

		}

		catch (WebClientResponseException ex) {

			if (ex.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY)
				throw new UnprocessableException("operation.cannot.be.performed.on.deleted.user");

			if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED)
				throw new UnAuthorizedException("deleted.user.cannot.perform.this.operation");
			if (ex.getStatusCode() == HttpStatus.NOT_FOUND)
				throw new ResourceNotFoundException("user.not.found");
		} catch (Exception e) {
			e.printStackTrace();

			throw new InvalidDataException("error.from.jsonprocessing");
		}

		return responseDto;
	}

	@Override
	public UserResponseDto deleteUser(Long userId, String jwtToken) {

		UserProfile userProfile = this.userProfileRepo.findByUserId(userId)
				.orElseThrow((() -> new ResourceNotFoundException("user.not.found")));
		UserResponseDto responseDto = new UserResponseDto();

		try {

			WebClient webClient = webClientBuilder.build();
			Mono<String> resultMono = webClient.delete()
//					.uri("http://localhost:8081/auth/v1/admin/users/{userId}", userId).header(HttpHeaders.AUTHORIZATION, jwtToken)
					.uri(authorisationUrl + "/auth/v1/admin/users/{userId}", userId).header(HttpHeaders.AUTHORIZATION, jwtToken)
					.retrieve().bodyToMono(String.class);

			String result = resultMono.block();

			JsonNode rootNode = objectMapper.readTree(result);

			JsonNode dataNode = rootNode.get("data");

			long tempUserId = dataNode.get("userId").asLong();
			boolean isVerified = dataNode.get("isVerified").asBoolean();
			boolean isActive = dataNode.get("isActive").asBoolean();

			JsonNode rolesNode = dataNode.get("roles");
			
			String createdAt = dataNode.get("createdAt").asText();
            String updatedAt = dataNode.get("updatedAt").asText();

			Set<String> roles = new HashSet<>();
			if (rolesNode.isArray()) {
				for (JsonNode role : rolesNode) {
					roles.add(role.asText());
				}
			}

			responseDto = this.userModelMapper.authServiceToResponseDto(userProfile, tempUserId, isActive, isVerified,
					roles, createdAt, updatedAt);

		} catch (WebClientResponseException ex) {

			if (ex.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY)
				throw new UnprocessableException("deleted.user.cannot.perform.this.operation");

			if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED)
				throw new UnAuthorizedException("user.not.authorized");
			if (ex.getStatusCode() == HttpStatus.NOT_FOUND)
				throw new ResourceNotFoundException("user.not.found");
		} catch (JsonProcessingException e) {

			throw new InvalidDataException("error.from.jsonprocessing");
		}

		return responseDto;

	}

	public UserResponseDto blockUser(Long userId, BlockUserRequestDto requestDto, String jwtToken) {

		UserResponseDto responseDto = new UserResponseDto();
		Map<String, Object> authRequest = new HashMap<>();

		UserProfile userProfile = this.userProfileRepo.findByUserId(userId)
				.orElseThrow((() -> new ResourceNotFoundException("user.not.found")));

		authRequest.put("isActive", requestDto.getIsActive());

		try {

			WebClient webClient = webClientBuilder.build();
			Mono<String> responseMono = webClient.put()
//					.uri("http://localhost:8081/auth/v1/admin/users/block/{userId}", userId)
					.uri(authorisationUrl + "/auth/v1/admin/users/block/{userId}", userId)
					.header(HttpHeaders.AUTHORIZATION, jwtToken).body(BodyInserters.fromValue(authRequest)).retrieve()
					.bodyToMono(String.class);

			String result = responseMono.block();

			JsonNode rootNode = objectMapper.readTree(result);

			JsonNode dataNode = rootNode.get("data");

			long tempUserId = dataNode.get("userId").asLong();
			boolean isVerified = dataNode.get("isVerified").asBoolean();
			boolean isActive = dataNode.get("isActive").asBoolean();

			JsonNode rolesNode = dataNode.get("roles");
			
			String createdAt = dataNode.get("createdAt").asText();
            String updatedAt = dataNode.get("updatedAt").asText();

			Set<String> roles = new HashSet<>();
			if (rolesNode.isArray()) {
				for (JsonNode role : rolesNode) {
					roles.add(role.asText());
					System.out.println("In try roles : " + roles);
				}
			}

			responseDto = this.userModelMapper.authServiceToResponseDto(userProfile, tempUserId, isActive, isVerified,
					roles, createdAt, updatedAt);

		}

		catch (WebClientResponseException ex) {

			if (ex.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY)
				throw new UnprocessableException("operation.cannot.be.performed.on.deleted.user");

			if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED)
				throw new UnAuthorizedException("deleted.user.cannot.perform.this.operation");
			if (ex.getStatusCode() == HttpStatus.NOT_FOUND)
				throw new ResourceNotFoundException("user.not.found");
		} catch (Exception e) {
			e.printStackTrace();

			throw new InvalidDataException("error.from.jsonprocessing");
		}

		return responseDto;
	}

}
