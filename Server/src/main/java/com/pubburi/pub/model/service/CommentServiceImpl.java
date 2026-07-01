package com.pubburi.pub.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pubburi.pub.model.dao.CommentDao;
import com.pubburi.pub.model.dto.Comment;

@Service
public class CommentServiceImpl implements CommentService {

	@Autowired
	private CommentDao commentDao;

	@Override
	public List<Comment> getCommentsByProductId(int productId) {
		if (productId <= 0) {
			return List.of();
		}
		return commentDao.selectByProductId(productId);
	}

	@Override
	public List<Comment> getAllComments() {
		return commentDao.selectAll();
	}

	@Override
	@Transactional
	public int addComment(Comment comment) {
		if (comment == null || comment.getUserId() == null || comment.getProductId() <= 0 || comment.getRating() < 0) {
			return 0;
		}
		return commentDao.insert(comment);
	}

	@Override
	public int updateComment(Comment comment) {
		if (comment == null || comment.getId() <= 0) {
			return 0;
		}
		return commentDao.update(comment);
	}

	@Override
	public int removeComment(int id) {
		if (id <= 0) {
			return 0;
		}
		return commentDao.delete(id);
	}

	@Override
	public Comment getCommentById(int commentId) {
		if (commentId <= 0) {
			return null;
		}
		return commentDao.select(commentId);
	}
}
