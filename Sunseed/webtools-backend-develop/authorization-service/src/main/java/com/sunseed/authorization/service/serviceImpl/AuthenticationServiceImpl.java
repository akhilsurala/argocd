package com.sunseed.authorization.service.serviceImpl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.sunseed.authorization.service.entity.Role;
import com.sunseed.authorization.service.entity.User;
import com.sunseed.authorization.service.enums.RoleType;
import com.sunseed.authorization.service.exceptions.AuthenticationException;
import com.sunseed.authorization.service.mappers.UserMapper;
import com.sunseed.authorization.service.model.requestDTO.AdminSignupRequestDto;
import com.sunseed.authorization.service.model.requestDTO.LoginRequestDto;
import com.sunseed.authorization.service.model.requestDTO.SignupRequestDto;
import com.sunseed.authorization.service.model.responseDTO.AuthorizationResponseDto;
import com.sunseed.authorization.service.model.responseDTO.LoginResponseDto;
import com.sunseed.authorization.service.model.responseDTO.SignupResponseDto;
import com.sunseed.authorization.service.model.responseDTO.UserAuthResponseDto;
import com.sunseed.authorization.service.repository.RoleRepository;
import com.sunseed.authorization.service.repository.UserRepository;
import com.sunseed.authorization.service.service.AuthenticationService;
import com.sunseed.authorization.service.service.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final AuthenticationManager authenticationManager;
	private final UserMapper userMapper;
	private final JwtService jwtService;
	private final PasswordEncoder passwordEncoder;

	public SignupResponseDto signup(SignupRequestDto request) {

		var existingUser = userRepository.findByEmailId(request.getEmailId());
		if (existingUser.isPresent()) {
			// user already exists returning directly not waiting
			UserAuthResponseDto userAuthResponseDto = userToUserAuthResponseDto(existingUser.get());
			SignupResponseDto signupResponse = SignupResponseDto.builder().user(userAuthResponseDto).build();
			throw new AuthenticationException(signupResponse, "User already registered", HttpStatus.CONFLICT);
		} else {
			// proceeding to attempt user registration
			return attemptRegisterUser(request);
		}
	}

	public SignupResponseDto adminSignup(AdminSignupRequestDto request) {

		if (request == null || request.getEmailId() == null || request.getEmailId().isEmpty()
				|| request.getPassword() == null || request.getPassword().isEmpty())
			throw new AuthenticationException("Email and Password should not be null or empty", HttpStatus.BAD_REQUEST);

		var existingUser = userRepository.findByEmailId(request.getEmailId());
		if (existingUser.isPresent()) {
			// user already exists returning directly not waiting
			UserAuthResponseDto userAuthResponseDto = userMapper.userToUserAuthResponseDto(existingUser.get());
			SignupResponseDto signupResponse = SignupResponseDto.builder().user(userAuthResponseDto).build();
			throw new AuthenticationException(signupResponse, "User already registered", HttpStatus.CONFLICT);
		} else {
			// proceeding to attempt user registration
			System.out.println("eneterd here in adminSignupImpl");
			var newUser = userMapper.registerRequestDtoToUserAdmin(request);
			Set<Role> userRoles = new HashSet<>();
			Set<RoleType> roleTypeList = RoleType.getListOfRoleTypes(request.getRoles());
			System.out.println(roleTypeList);
			for (RoleType e : roleTypeList) {
				Role existingRole = roleRepository.findByRoleType(e)
						.orElseThrow(() -> new AuthenticationException("Role not found", HttpStatus.NOT_FOUND));
				System.out.println("Existing role : " + existingRole.getId());
				userRoles.add(existingRole);
			}

			userRoles.forEach(t -> System.out.println(t.getRoleType()));

			newUser.setRoles(userRoles);
			var savedUser = userRepository.save(newUser);
			savedUser.getRoles().forEach(t -> System.out.println("Saved user " + t.getRoleType()));
			UserAuthResponseDto userAuthResponseDto = userMapper.userToUserAuthResponseDto(savedUser);
			SignupResponseDto signupResponse = SignupResponseDto.builder().user(userAuthResponseDto).build();
			return signupResponse;
		}
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@Retryable(maxAttempts = 3, backoff = @Backoff(delay = 100))
	private SignupResponseDto attemptRegisterUser(SignupRequestDto request) {
		try {
			// double checking user existence to handle race conditions
			var existingUser = userRepository.findByEmailId(request.getEmailId());

			if (existingUser.isPresent()) {
				// If user was created by another thread while waiting to acquire the lock
				UserAuthResponseDto userAuthResponseDto = userToUserAuthResponseDto(existingUser.get());
				SignupResponseDto signupResponse = SignupResponseDto.builder().user(userAuthResponseDto).build();
				throw new AuthenticationException(signupResponse, "User already registered", HttpStatus.CONFLICT);

			} else {
				// Safe to create new user
				var newUser = registerRequestDtoToUser(request);
				var savedUser = userRepository.save(newUser);
				UserAuthResponseDto userAuthResponseDto = userToUserAuthResponseDto(savedUser);
				SignupResponseDto signupResponse = SignupResponseDto.builder().user(userAuthResponseDto).build();
				return signupResponse;
			}

		} catch (DataIntegrityViolationException e) {
			// handling potential race condition multiple threads trying to insert same user
			// concurrently
			throw new AuthenticationException("User registration failed", HttpStatus.INTERNAL_SERVER_ERROR, e);
		} catch (Exception e) {
			throw new AuthenticationException("Internal Server error", HttpStatus.INTERNAL_SERVER_ERROR, e);
		}
	}

	public LoginResponseDto login(LoginRequestDto request) {

		Authentication auth = null;
		try {
			auth = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(request.getEmailId(), request.getPassword()));
		} catch (UsernameNotFoundException e) {
			throw new AuthenticationException("User not found", HttpStatus.NOT_FOUND, e);
		} catch (BadCredentialsException e) {
			throw new AuthenticationException("Invalid Credentials", HttpStatus.UNAUTHORIZED, e);
		} catch (org.springframework.security.core.AuthenticationException e) {
			throw new AuthenticationException("Internal Server error", HttpStatus.INTERNAL_SERVER_ERROR, e);
		} catch (Exception e) {
			throw new AuthenticationException("Internal Server error", HttpStatus.INTERNAL_SERVER_ERROR, e);
		}

		UserDetails userDetails = (UserDetails) auth.getPrincipal();
		var existingUser = userRepository.findByEmailId(userDetails.getUsername());
		if (existingUser.isPresent() && existingUser.get().getIsActive() == false)
			throw new AuthenticationException("User not found", HttpStatus.NOT_FOUND);

		UserAuthResponseDto userAuthResponseDto = userToUserAuthResponseDto(existingUser.get());

		Set<Role> existingUserRoles = existingUser.get().getRoles();
		Set<String> values = new HashSet<>();

		if (request.getSignInAs().equalsIgnoreCase("user")) {
			values.add("user");
		}
		if (request.getSignInAs().equalsIgnoreCase("admin")) {
			values.add("admin");
		}

		Set<Role> rolesFromLogin = RoleType.getListOfRoleTypes(values).stream()
				.map(roleType -> roleRepository.findByRoleType(roleType).orElseThrow(
						() -> new AuthenticationException("Role not found: " + roleType.name(), HttpStatus.NOT_FOUND)))
				.collect(Collectors.toSet());
		
		boolean isRoleMatched = containsAnyRole(rolesFromLogin,existingUserRoles);
		if(!isRoleMatched)
			throw new AuthenticationException("Invalid credentials",HttpStatus.UNPROCESSABLE_ENTITY);

		if (existingUser.get().getIsVerified() == true) {
			var jwtToken = jwtService.generateToken(existingUser.get());
			var data = LoginResponseDto.builder().accessToken(jwtToken).user(userAuthResponseDto).build();
			return data;
		}
		var data = LoginResponseDto.builder().user(userAuthResponseDto).build();
		return data;
	}

	public AuthorizationResponseDto authorize(String coreRequestURI) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getPrincipal();
		if (coreRequestURI.startsWith("/v1/admin")) {
			boolean hasAdminRole = hasAdminRole(user);
			if (!hasAdminRole)
				throw new AuthenticationException(user, "User is unauthorized to perform requested operation",
						HttpStatus.FORBIDDEN);
		}
		AuthorizationResponseDto authorizationResponseDto = userToAuthorizationResponseDto(user);
		return authorizationResponseDto;
	}

	public UserAuthResponseDto getUser(String emailId) {

		if (emailId == null || emailId.isEmpty())
			throw new AuthenticationException("Email ID should not be null or empty", HttpStatus.BAD_REQUEST);
		Optional<User> user = null;
		user = userRepository.findByEmailId(emailId.toLowerCase());

		if (user.isEmpty())
			throw new AuthenticationException("User not found", HttpStatus.NOT_FOUND);

		UserAuthResponseDto userResponse = userToUserAuthResponseDto(user.get());
		return userResponse;

	}

	@Override
	public LoginResponseDto verifyUser(String emailId) {
		if (emailId == null || emailId.isEmpty())
			throw new AuthenticationException("Email ID should not be null or empty", HttpStatus.BAD_REQUEST);
		Optional<User> user = userRepository.findByEmailId(emailId);
		if (user.isEmpty())
			throw new AuthenticationException("User not found", HttpStatus.NOT_FOUND);
		user.get().setIsVerified(true);
		User savedUser = userRepository.save(user.get());
		UserAuthResponseDto userResponse = userToUserAuthResponseDto(savedUser);
		String accessToken = jwtService.generateToken(savedUser);
		LoginResponseDto loginResponse = LoginResponseDto.builder().user(userResponse).accessToken(accessToken).build();
		return loginResponse;
	}

	@Override
	public UserAuthResponseDto resetPassword(String emailId, String password) {

		Optional<User> user = userRepository.findByEmailId(emailId);
		if (user.isEmpty())
			throw new AuthenticationException("User not found", HttpStatus.NOT_FOUND);
		// set new password logic
		User getUser = user.get();
		getUser.setPassword(passwordEncoder.encode(password));
		getUser.setIsVerified(true);
		User savedUser = userRepository.save(getUser);

		UserAuthResponseDto userResponse = userToUserAuthResponseDto(savedUser);
		return userResponse;
	}

	private UserAuthResponseDto userToUserAuthResponseDto(User user) {

		UserAuthResponseDto userAuthResponseDto = new UserAuthResponseDto();
		userAuthResponseDto.setUserId(user.getId());
		userAuthResponseDto.setEmailId(user.getEmailId().toLowerCase());
		userAuthResponseDto.setIsVerified(user.getIsVerified());
		userAuthResponseDto
				.setRoles(user.getRoles().stream().map(role -> role.getRoleType()).collect(Collectors.toSet()));
		return userAuthResponseDto;
	}

	private User registerRequestDtoToUser(SignupRequestDto request) {

		User user = new User();
		user.setEmailId(request.getEmailId());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		Optional<Role> defaultRole = roleRepository.findByRoleType(RoleType.USER);

		// setting the default role as USER
		if (defaultRole.isEmpty()) {
			AuthenticationException e = new AuthenticationException(null, "default role not found",
					HttpStatus.NOT_FOUND);
			e.printStackTrace();
			throw e;
		}

		Set<Role> userRoles = new HashSet<>();
		userRoles.add(defaultRole.get());

		user.setRoles(userRoles);
		return user;
	}

	private AuthorizationResponseDto userToAuthorizationResponseDto(User user) {

		AuthorizationResponseDto authorizationResponseDto = new AuthorizationResponseDto();
		authorizationResponseDto.setEmailId(user.getEmailId());
		authorizationResponseDto.setUserId(user.getId());
		authorizationResponseDto
				.setRoles(user.getRoles().stream().map(role -> role.getRoleType()).collect(Collectors.toSet()));
		return authorizationResponseDto;
	}

	private boolean hasAdminRole(User user) {
		return user.getRoles().stream().anyMatch(role -> role.getRoleType() == RoleType.ADMIN);
	}
	
	public boolean containsAnyRole(Set<Role> setA, Set<Role> setB) {
		for (Role element : setB) {
			if (setA.contains(element)) {
				return true;
			}
		}
		return false;
	}
}
