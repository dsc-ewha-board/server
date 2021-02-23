package com.february.edsc.controller;

import com.february.edsc.common.Error;
import com.february.edsc.common.ErrorMessage;
import com.february.edsc.domain.user.User;
import com.february.edsc.domain.user.UserUpdateDto;
import com.february.edsc.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Controller
@AllArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/users/{id}")
	public ResponseEntity<Object> getUser(@PathVariable Long id) {
		Optional<User> user = userService.findById(id);
		if (user.isEmpty()) {
			return ResponseEntity.badRequest()
				.body(new Error(HttpStatus.BAD_REQUEST, ErrorMessage.NO_SUCH_USER));
		} return ResponseEntity.ok().body(user.get().toUserDetailResponseDto());
	}

	@PutMapping("/users/{id}")
	public ResponseEntity<Object> updateUser(@PathVariable Long id,
		@RequestBody UserUpdateDto userUpdateDto) {
		if (userUpdateDto.isRequiredFieldNull())
			return ResponseEntity.badRequest()
				.body(new Error(HttpStatus.BAD_REQUEST, ErrorMessage.REQUIRED_FIELD_NULL));
		Optional<User> user = userService.findById(id);
		if (user.isEmpty())
			return ResponseEntity.badRequest()
				.body(new Error(HttpStatus.BAD_REQUEST, ErrorMessage.NO_SUCH_USER));
		if (!user.get().getEmail().equals(userUpdateDto.getEmail()))
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(new Error(HttpStatus.FORBIDDEN, ErrorMessage.UNAUTHORIZED_TO_UPDATE));
		userService.updateUser(user.get(), userUpdateDto);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/users")
	public ResponseEntity<Object> getUsers() {
		return ResponseEntity.ok().body(userService.getUsers());
	}

	@GetMapping("/users/{id}/posts")
	public ResponseEntity<Object> getUserPosts(@PathVariable Long id) {
		Optional<User> user = userService.findById(id);
		if (user.isEmpty()) {
			return ResponseEntity.badRequest()
				.body(new Error(HttpStatus.BAD_REQUEST, ErrorMessage.NO_SUCH_USER));
		} return ResponseEntity.ok().body(userService.getUserPosts(id));
	}

	@GetMapping("/users/{id}/likes")
	public ResponseEntity<Object> getUserLikes(@PathVariable Long id) {
		Optional<User> user = userService.findById(id);
		if (user.isEmpty()) {
			return ResponseEntity.badRequest()
				.body(new Error(HttpStatus.BAD_REQUEST, ErrorMessage.NO_SUCH_USER));
		} return ResponseEntity.ok().body(userService.getUserLikes(id));
	}
}
