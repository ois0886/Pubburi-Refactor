package com.pubburi.pub.model.dao;

import java.util.List;

import com.pubburi.pub.model.dto.User;

public interface UserDao {
	List<User> selectAll();

	int insert(User user);

	int update(User user);

	int delete(String id);

	int updateStamp(User user);

	User selectById(String userId);
}
