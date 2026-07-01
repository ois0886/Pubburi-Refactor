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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pubburi.pub.model.dto.Product;
import com.pubburi.pub.model.service.ProductService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
@CrossOrigin(originPatterns = { "http://localhost:*", "http://127.0.0.1:*" }, allowCredentials = "true")
public class ProductRestController extends SessionSupport {

	private final ProductService productService;

	public ProductRestController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping("/products")
	public ResponseEntity<List<Product>> products(@RequestParam(required = false) String type,
			@RequestParam(required = false) String q, @RequestParam(defaultValue = "popular") String sort) {
		return ResponseEntity.ok(productService.searchProducts(type, q, sort));
	}

	@GetMapping("/products/popular")
	public ResponseEntity<List<Product>> popularProducts() {
		return ResponseEntity.ok(productService.getPopularProducts());
	}

	@GetMapping("/products/{id}")
	public ResponseEntity<Product> product(@PathVariable int id) {
		Product product = productService.getProductById(id);
		return product == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(product);
	}

	@PostMapping("/admin/products")
	public ResponseEntity<Boolean> addProduct(@RequestBody Product product, HttpSession session) {
		requireAdmin(session);
		return ResponseEntity.ok(productService.addProduct(product) > 0);
	}

	@PutMapping("/admin/products/{id}")
	public ResponseEntity<Boolean> updateProduct(@PathVariable int id, @RequestBody Product product,
			HttpSession session) {
		requireAdmin(session);
		product.setId(id);
		return ResponseEntity.ok(productService.updateProduct(product) > 0);
	}

	@DeleteMapping("/admin/products/{id}")
	public ResponseEntity<Boolean> deleteProduct(@PathVariable int id, HttpSession session) {
		requireAdmin(session);
		return ResponseEntity.ok(productService.removeProduct(id) > 0);
	}
}
