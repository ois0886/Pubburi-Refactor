package com.pubburi.pub.controller.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pubburi.pub.model.dto.OrderInfo;
import com.pubburi.pub.model.dto.User;
import com.pubburi.pub.model.service.OrderService;
import com.pubburi.pub.model.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
@CrossOrigin(originPatterns = { "http://localhost:*", "http://127.0.0.1:*" }, allowCredentials = "true")
public class UserRestController extends SessionSupport {

	private final UserService userService;
	private final OrderService orderService;

	public UserRestController(UserService userService, OrderService orderService) {
		this.userService = userService;
		this.orderService = orderService;
	}

	@PostMapping("/auth/login")
	public ResponseEntity<User> login(@RequestBody User loginUser, HttpSession session) {
		User user = userService.login(loginUser);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		session.setAttribute(SESSION_USER, user);
		return ResponseEntity.ok(user);
	}

	@PostMapping("/auth/logout")
	public ResponseEntity<Void> logout(HttpSession session) {
		session.invalidate();
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/auth/me")
	public ResponseEntity<User> me(HttpSession session) {
		User user = currentUser(session);
		return user == null ? ResponseEntity.status(HttpStatus.UNAUTHORIZED).build() : ResponseEntity.ok(user);
	}

	@PostMapping("/users")
	public ResponseEntity<Boolean> joinUser(@RequestBody User user) {
		user.setRole("USER");
		int result = userService.addUser(user);
		return result > 0 ? ResponseEntity.status(HttpStatus.CREATED).body(true) : ResponseEntity.badRequest().body(false);
	}

	@GetMapping("/users/id-available")
	public ResponseEntity<Map<String, Boolean>> isAvailable(@RequestParam String id) {
		return ResponseEntity.ok(Map.of("available", !userService.isUsedId(id)));
	}

	@GetMapping("/users/{id}/profile")
	public ResponseEntity<Map<String, Object>> profile(@PathVariable String id, HttpSession session) {
		requireSelfOrAdmin(session, id);
		User user = userService.getUserById(id);
		if (user == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
		}
		List<OrderInfo> orders = orderService.getOrdersByUserId(id);
		Map<String, Object> result = new HashMap<>();
		result.put("user", user);
		result.put("orders", orders);
		result.put("grade", getGrade(user.getStamps()));
		return ResponseEntity.ok(result);
	}

	@GetMapping("/admin/users")
	public ResponseEntity<List<User>> users(HttpSession session) {
		requireAdmin(session);
		return ResponseEntity.ok(userService.getUserList());
	}

	@PutMapping("/admin/users/{id}")
	public ResponseEntity<Boolean> updateUser(@PathVariable String id, @RequestBody User user, HttpSession session) {
		requireAdmin(session);
		user.setId(id);
		return ResponseEntity.ok(userService.modifyUser(user) > 0);
	}

	@DeleteMapping("/admin/users/{id}")
	public ResponseEntity<Boolean> deleteUser(@PathVariable String id, HttpSession session) {
		requireAdmin(session);
		return ResponseEntity.ok(userService.removeUser(id) > 0);
	}

	private Map<String, Object> getGrade(Integer stamp) {
		List<Level> levels = new ArrayList<>();
		levels.add(new Level("", 0, 0, ""));
		levels.add(new Level("씨앗", 10, 50, "seeds.png"));
		levels.add(new Level("꽃", 15, 125, "flower.png"));
		levels.add(new Level("열매", 20, 225, "coffee_fruit.png"));
		levels.add(new Level("커피콩", 25, 350, "coffee_beans.png"));
		levels.add(new Level("커피나무", Integer.MAX_VALUE, Integer.MAX_VALUE, "coffee_tree.png"));

		int safeStamp = stamp == null ? 0 : stamp;
		Map<String, Object> grade = new HashMap<>();
		for (int i = 1; i <= 5; i++) {
			if (safeStamp <= levels.get(i).max()) {
				grade.put("img", levels.get(i).img());
				grade.put("step", (int) Math.ceil((double) (safeStamp - levels.get(i - 1).max()) / levels.get(i).unit()));
				grade.put("stepMax", levels.get(i).unit());
				grade.put("to", (levels.get(i).max() - safeStamp) % levels.get(i).unit() + 1);
				grade.put("title", levels.get(i).title());
				break;
			}
		}
		return grade;
	}

	private record Level(String title, int unit, int max, String img) {
	}
}
