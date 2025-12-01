package com.ecommerce.project.controller;

import com.ecommerce.project.dto.ReviewRequestDTO;
import com.ecommerce.project.entity.Review;
import com.ecommerce.project.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> add(@RequestBody ReviewRequestDTO dto) {
        return ResponseEntity.status(201).body(reviewService.addReview(dto));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> productReviews(@PathVariable String productId) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> userReviews(@PathVariable String userId) {
        return ResponseEntity.ok(reviewService.getUserReviews(userId));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<Review> update(@PathVariable String reviewId, @RequestBody UpdateReviewRequest req) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, req.rating(), req.comment()));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> delete(@PathVariable String reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    record UpdateReviewRequest(int rating, String comment) {}
}
