package com.kasper;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000") // Allow frontend CORS
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ✅ GET All Products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // ✅ GET Product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable int id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    // ✅ POST - Add Product
    @PostMapping
    public ResponseEntity<String> addProduct(@Valid @RequestBody Product product) {
        productService.addProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body("Product added successfully!");
    }

    // ✅ PUT - Update Product
    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable int id, @Valid @RequestBody Product product) {
        productService.updateProduct(id, product);
        return ResponseEntity.ok("Product updated successfully!");
    }

    // ✅ DELETE - Remove Product
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully!");
    }
}
