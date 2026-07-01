package com.pubburi.pub.controller.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.pubburi.pub.model.dto.User;

import jakarta.servlet.http.HttpSession;

abstract class SessionSupport {
	static final String SESSION_USER = "PUBBURI_USER";

	User currentUser(HttpSession session) {
		Object value = session == null ? null : session.getAttribute(SESSION_USER);
		return value instanceof User user ? user : null;
	}

	User requireLogin(HttpSession session) {
		User user = currentUser(session);
		if (user == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
		}
		return user;
	}

	User requireAdmin(HttpSession session) {
		User user = requireLogin(session);
		if (!user.isAdmin()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin permission required");
		}
		return user;
	}

	void requireSelfOrAdmin(HttpSession session, String userId) {
		User user = requireLogin(session);
		if (!user.isAdmin() && !user.getId().equals(userId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
		}
	}
}
