package com.pubburi.pub.controller.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pubburi.pub.model.dto.Market;
import com.pubburi.pub.model.service.MarketService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
@CrossOrigin(originPatterns = { "http://localhost:*", "http://127.0.0.1:*" }, allowCredentials = "true")
public class MarketRestController extends SessionSupport {

	private final MarketService marketService;

	public MarketRestController(MarketService marketService) {
		this.marketService = marketService;
	}

	@GetMapping("/markets")
	public ResponseEntity<List<Market>> markets() {
		return ResponseEntity.ok(marketService.getMarketList());
	}

	@GetMapping("/markets/{id}")
	public ResponseEntity<Market> market(@PathVariable int id) {
		Market market = marketService.getMarketById(id);
		return market == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(market);
	}

	@PostMapping("/admin/markets")
	public ResponseEntity<Boolean> addMarket(@RequestBody Market market, HttpSession session) {
		requireAdmin(session);
		return ResponseEntity.ok(marketService.addMarket(market) > 0);
	}

	@PutMapping("/admin/markets/{id}")
	public ResponseEntity<Boolean> updateMarket(@PathVariable int id, @RequestBody Market market, HttpSession session) {
		requireAdmin(session);
		market.setId(id);
		return ResponseEntity.ok(marketService.updateMarket(market) > 0);
	}

	@DeleteMapping("/admin/markets/{id}")
	public ResponseEntity<Boolean> deleteMarket(@PathVariable int id, HttpSession session) {
		requireAdmin(session);
		return ResponseEntity.ok(marketService.removeMarket(id) > 0);
	}
}
