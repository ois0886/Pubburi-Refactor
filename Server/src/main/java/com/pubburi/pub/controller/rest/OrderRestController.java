package com.pubburi.pub.controller.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pubburi.pub.model.dto.Order;
import com.pubburi.pub.model.dto.OrderInfo;
import com.pubburi.pub.model.dto.User;
import com.pubburi.pub.model.service.OrderService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
@CrossOrigin(originPatterns = { "http://localhost:*", "http://127.0.0.1:*" }, allowCredentials = "true")
public class OrderRestController extends SessionSupport {

	private final OrderService orderService;

	public OrderRestController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping("/orders")
	public ResponseEntity<Order> makeOrder(@RequestBody Order order, HttpSession session) {
		User user = requireLogin(session);
		if (order.getUserId() == null || order.getUserId().isBlank()) {
			order.setUserId(user.getId());
		}
		requireSelfOrAdmin(session, order.getUserId());
		int result = orderService.makeOrder(order, order.getDetails());
		return result > 0 ? ResponseEntity.status(HttpStatus.CREATED).body(order) : ResponseEntity.badRequest().build();
	}

	@GetMapping("/users/{userId}/orders")
	public ResponseEntity<List<OrderInfo>> userOrders(@PathVariable String userId, HttpSession session) {
		requireSelfOrAdmin(session, userId);
		return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
	}

	@GetMapping("/orders/{id}")
	public ResponseEntity<OrderInfo> order(@PathVariable int id, HttpSession session) {
		OrderInfo order = orderService.getOrderInfo(id);
		if (order == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
		}
		requireSelfOrAdmin(session, order.getUserId());
		return ResponseEntity.ok(order);
	}

	@GetMapping("/admin/orders")
	public ResponseEntity<List<OrderInfo>> allOrders(HttpSession session) {
		requireAdmin(session);
		return ResponseEntity.ok(orderService.getAllOrders());
	}

	@PatchMapping("/admin/orders/{id}/complete")
	public ResponseEntity<Boolean> complete(@PathVariable int id, HttpSession session) {
		requireAdmin(session);
		Order order = orderService.getOrderById(id);
		if (order == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
		}
		order.setCompleted("Y");
		return ResponseEntity.ok(orderService.updateOrder(order) > 0);
	}

	@DeleteMapping("/admin/orders/{id}")
	public ResponseEntity<Boolean> deleteOrder(@PathVariable int id, HttpSession session) {
		requireAdmin(session);
		return ResponseEntity.ok(orderService.removeOrder(id) > 0);
	}
}
