package com.pubburi.pub.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pubburi.pub.controller.api.PageCriteria;
import com.pubburi.pub.controller.api.PageResponse;
import com.pubburi.pub.model.dao.OrderDao;
import com.pubburi.pub.model.dao.OrderDetailDao;
import com.pubburi.pub.model.dao.ProductDao;
import com.pubburi.pub.model.dao.StampDao;
import com.pubburi.pub.model.dao.UserDao;
import com.pubburi.pub.model.dto.Order;
import com.pubburi.pub.model.dto.OrderDetail;
import com.pubburi.pub.model.dto.OrderDetailInfo;
import com.pubburi.pub.model.dto.OrderInfo;
import com.pubburi.pub.model.dto.Stamp;
import com.pubburi.pub.model.dto.User;

@Service
public class OrderServiceImpl implements OrderService {

	private final OrderDao orderDao;
	private final OrderDetailDao orderDetailDao;
	private final StampDao stampDao;
	private final UserDao userDao;
	private final ProductDao productDao;

	public OrderServiceImpl(OrderDao orderDao, OrderDetailDao orderDetailDao, StampDao stampDao, UserDao userDao,
			ProductDao productDao) {
		this.orderDao = orderDao;
		this.orderDetailDao = orderDetailDao;
		this.stampDao = stampDao;
		this.userDao = userDao;
		this.productDao = productDao;
	}

	@Override
	@Transactional
	public int makeOrder(Order order, List<OrderDetail> details) {
		if (order == null || isBlank(order.getUserId()) || details == null || details.isEmpty()) {
			return 0;
		}
		if (isBlank(order.getCompleted())) {
			order.setCompleted("N");
		}
		int result = orderDao.insert(order);
		int totalQuantity = 0;
		for (OrderDetail detail : details) {
			if (detail.getProductId() <= 0 || detail.getQuantity() <= 0) {
				throw new IllegalArgumentException("Invalid order detail");
			}
			detail.setOrderId(order.getoId());
			orderDetailDao.insert(detail);
			productDao.incrementOrderCount(detail.getProductId(), detail.getQuantity());
			totalQuantity += detail.getQuantity();
		}
		Stamp stamp = new Stamp();
		stamp.setUserId(order.getUserId());
		stamp.setOrderId(order.getoId());
		stamp.setQuantity(totalQuantity);
		stampDao.insert(stamp);

		User user = userDao.selectById(order.getUserId());
		if (user != null) {
			user.setStamps(user.getStamps() + totalQuantity);
			userDao.updateStamp(user);
		}
		return result;
	}

	@Override
	public List<OrderInfo> getOrdersByUserId(String userId) {
		if (isBlank(userId)) {
			return List.of();
		}
		return orderDao.selectOrderInfoListByUserId(userId);
	}

	@Override
	public PageResponse<OrderInfo> getOrdersByUserId(String userId, PageCriteria criteria) {
		if (isBlank(userId)) {
			return PageResponse.of(List.of(), criteria, 0);
		}
		List<OrderInfo> orders = orderDao.selectOrderInfoPageByUserId(userId, criteria.size(), criteria.offset());
		return PageResponse.of(orders, criteria, orderDao.countOrderInfoByUserId(userId));
	}

	@Override
	public List<OrderInfo> getAllOrders() {
		return orderDao.selectAllOrderInfo();
	}

	@Override
	public PageResponse<OrderInfo> getAllOrders(PageCriteria criteria) {
		List<OrderInfo> orders = orderDao.selectAllOrderInfoPaged(criteria.size(), criteria.offset());
		return PageResponse.of(orders, criteria, orderDao.countAllOrderInfo());
	}

	@Override
	public Order getOrderById(int oId) {
		if (oId <= 0) {
			return null;
		}
		return orderDao.selectById(oId);
	}

	@Override
	public int updateOrder(Order order) {
		if (order == null || order.getoId() <= 0) {
			return 0;
		}
		return orderDao.update(order);
	}

	@Override
	public int removeOrder(int oId) {
		if (oId <= 0) {
			return 0;
		}
		return orderDao.delete(oId);
	}

	@Override
	public OrderInfo getOrderInfo(int oId) {
		if (oId <= 0) {
			return null;
		}
		return orderDao.selectOrderInfo(oId);
	}

	@Override
	public List<OrderInfo> getLastMonthOrder(String id) {
		return isBlank(id) ? List.of() : orderDao.getLastMonthOrder(id);
	}

	@Override
	public List<OrderInfo> getLast6MonthOrder(String id) {
		return isBlank(id) ? List.of() : orderDao.getLast6MonthOrder(id);
	}

	@SuppressWarnings("unused")
	private void attachDetails(OrderInfo orderInfo) {
		List<OrderDetailInfo> details = orderDao.getOrderDetailInfo(orderInfo.getoId());
		orderInfo.setDetails(details);
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}
}
