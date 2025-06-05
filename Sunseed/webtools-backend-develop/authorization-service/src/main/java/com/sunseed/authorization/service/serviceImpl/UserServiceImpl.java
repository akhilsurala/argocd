package com.sunseed.authorization.service.serviceImpl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.sunseed.authorization.service.entity.Role;
import com.sunseed.authorization.service.entity.User;
import com.sunseed.authorization.service.enums.RoleType;
import com.sunseed.authorization.service.exceptions.UserException;
import com.sunseed.authorization.service.mappers.UserMapper;
import com.sunseed.authorization.service.model.requestDTO.BlockUserRequestDto;
import com.sunseed.authorization.service.model.requestDTO.UserRequestDto;
import com.sunseed.authorization.service.model.responseDTO.UserResponseDto;
import com.sunseed.authorization.service.repository.RoleRepository;
import com.sunseed.authorization.service.repository.UserRepository;
import com.sunseed.authorization.service.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserMapper userMapper;

	@Override
	public UserResponseDto getUser(Long userId) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = (User) authentication.getPrincipal();

		if (!currentUser.getIsActive())
			throw new UserException("Deleted user cannot perform this operation", HttpStatus.FORBIDDEN);

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

		UserResponseDto response = userMapper.userToResponseDto(user);

		return response;
	}

	@Override
	public List<UserResponseDto> getAllUsers(String searchTitle) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = (User) authentication.getPrincipal();

		if (!currentUser.getIsActive())
			throw new UserException("Deleted user cannot perform this operation", HttpStatus.FORBIDDEN);

		if (userIsAdmin(currentUser.getId())) {

			List<User> allUsers;
		      if(searchTitle != null) {
		    	  allUsers = userRepository.getAllUsersWithSearchTitle(searchTitle.toString());
		      }else {
		    	  allUsers = userRepository.findAll();
		      }

			List<UserResponseDto> userDtoList = allUsers.stream()
					.sorted((user1, user2) -> user2.getCreatedAt().compareTo(user1.getCreatedAt()))
					.map(user -> userMapper.userToResponseDto(user)).collect(Collectors.toList());

			return userDtoList;
		}

		throw new UserException("User is not authorized to perform this operation", HttpStatus.FORBIDDEN);
	}

	@Override
	public UserResponseDto updateUser(Long targetUserId, UserRequestDto requestDto) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User adminUser = (User) authentication.getPrincipal();

		if (!adminUser.getIsActive()) {
			throw new UserException("Deleted user cannot perform this operation", HttpStatus.FORBIDDEN);
		}

		Optional<User> currentUser = userRepository.findById(targetUserId);
		if (currentUser.isEmpty()) {
			throw new UserException("User not found with id: " + targetUserId, HttpStatus.NOT_FOUND);
		}

		User user = currentUser.get();

		if (!user.getIsVerified() && !user.getEmailId().equalsIgnoreCase(requestDto.getEmailId())) {
			user.setEmailId(requestDto.getEmailId());
		}

		Set<RoleType> roleTypes = RoleType.getListOfRoleTypes(requestDto.getRoles());

		Set<Role> newRoles = roleTypes.stream()
				.map(roleType -> roleRepository.findByRoleType(roleType).orElseThrow(
						() -> new UserException("Role not found: " + roleType.name(), HttpStatus.NOT_FOUND)))
				.collect(Collectors.toSet());

		user.setRoles(newRoles);

		User updatedUser = userRepository.save(user);
		return userMapper.userToResponseDto(updatedUser);
	}

	@Override
	public UserResponseDto deleteUser(Long targetUserId) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User adminUser = (User) authentication.getPrincipal();

		if (!adminUser.getIsActive())
			throw new UserException("Deleted user cannot perform this operation", HttpStatus.FORBIDDEN);

		Optional<User> user = userRepository.findById(targetUserId);
		if (user.isEmpty())
			throw new UserException("User not found with id : " + targetUserId, HttpStatus.NOT_FOUND);

		User currentUser = user.get();
		currentUser.setIsActive(false);

		User updatedUser = this.userRepository.save(currentUser);

		UserResponseDto response = userMapper.userToResponseDto(updatedUser);

		return response;

	}

	public boolean userIsAdmin(Long userId) {
		Optional<User> userOptional = userRepository.findById(userId);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			String adminRole = RoleType.ADMIN.name();
			boolean hasAdminRole = user.getRoles().stream()
					.anyMatch(role -> role.getRoleType().name().equalsIgnoreCase(adminRole));
			return hasAdminRole;
		}
		return false;
	}

	@Override
	public UserResponseDto blockUser(Long userId, @Valid BlockUserRequestDto userRequestDto) {

//		if (!adminUser.isActive())
//			throw new UnprocessableException("Deleted user cannot perform this operation");

		Optional<User> user = userRepository.findById(userId);
		if (user.isEmpty())
			throw new UserException("User not found with id : " + userId, HttpStatus.NOT_FOUND);

		User currentUser = user.get();
		currentUser.setIsActive(userRequestDto.getIsActive());

		User updatedUser = this.userRepository.save(currentUser);

		UserResponseDto response = userMapper.userToResponseDto(updatedUser);

		return response;

	}

}