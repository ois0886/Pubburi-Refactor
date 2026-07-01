package com.pubburi.pub.model.service;

import java.util.List;

import com.pubburi.pub.model.dto.Market;

public interface MarketService {

	List<Market> getMarketList();

	Market getMarketById(int id);

	int addMarket(Market market);

	int updateMarket(Market market);

	int removeMarket(int id);

}
