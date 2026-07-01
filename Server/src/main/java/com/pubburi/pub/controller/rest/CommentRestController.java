package com.pubburi.pub.controller.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pubburi.pub.model.dto.Comment;
import com.pubburi.pub.model.dto.User;
import com.pubburi.pub.model.service.CommentService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
@CrossOrigin(originPatterns = { "http://localhost:*", "http://127.0.0.1:*" }, allowCredentials = "true")
public class CommentRestController extends SessionSupport {

	private final CommentService commentService;

	public CommentRestController(CommentService commentService) {
		this.commentService = commentService;
	}

	@GetMapping("/products/{productId}/comments")
	public ResponseEntity<List<Comment>> comments(@PathVariable int productId) {
		return ResponseEntity.ok(commentService.getCommentsByProductId(productId));
	}

	@PostMapping("/comments")
	public ResponseEntity<Integer> addComment(@RequestBody Comment comment, HttpSession session) {
		User user = requireLogin(session);
		if (comment.getUserId() == null || comment.getUserId().isBlank()) {
			comment.setUserId(user.getId());
		}
		requireSelfOrAdmin(session, comment.getUserId());
		return ResponseEntity.ok(commentService.addComment(comment));
	}

	@PutMapping("/comments/{id}")
	public ResponseEntity<Integer> updateComment(@PathVariable int id, @RequestBody Comment comment,
			HttpSession session) {
		Comment existing = commentService.getCommentById(id);
		if (existing == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");
		}
		requireSelfOrAdmin(session, existing.getUserId());
		comment.setId(id);
		return ResponseEntity.ok(commentService.updateComment(comment));
	}

	@DeleteMapping("/comments/{id}")
	public ResponseEntity<Integer> deleteComment(@PathVariable int id, HttpSession session) {
		Comment existing = commentService.getCommentById(id);
		if (existing == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");
		}
		requireSelfOrAdmin(session, existing.getUserId());
		return ResponseEntity.ok(commentService.removeComment(id));
	}

	@GetMapping("/admin/comments")
	public ResponseEntity<List<Comment>> allComments(HttpSession session) {
		requireAdmin(session);
		return ResponseEntity.ok(commentService.getAllComments());
	}
}
