package com.pubburi.pub.controller.response;

import java.util.List;
import java.util.Map;

public record ProfileResponse(UserResponse user, List<OrderResponse> orders, Map<String, Object> grade) {
}
