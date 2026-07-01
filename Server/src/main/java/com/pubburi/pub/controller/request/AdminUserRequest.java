package com.pubburi.pub.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record AdminUserRequest(
		@Size(max = 100) String name,
		@Size(min = 4, max = 100) String pass,
		@Min(0) Integer stamps,
		@Size(max = 20) String role) {
}
