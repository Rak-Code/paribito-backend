package com.ecommerce.project.service;

import com.ecommerce.project.dto.ReviewRequestDTO;
import com.ecommerce.project.entity.Review;
import com.ecommerce.project.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    @Override
    public Review addReview(ReviewRequestDTO dto) {

        if (dto.rating() < 1 || dto.rating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        Review review = new Review();
        review.setProductId(dto.productId());
        review.setUserId(dto.userId());
        review.setRating(dto.rating());
        review.setComment(dto.comment());

        Review saved = reviewRepository.save(review);
        log.info("Added review for product {} by user {}", dto.productId(), dto.userId());
        
        return saved;
    }

    @Override
    public Review getReview(String reviewId) {
        log.info("Fetching review with ID: {}", reviewId);
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
    }

    @Override
    public List<Review> getProductReviews(String productId) {
        log.info("Fetching reviews for product: {}", productId);
        return reviewRepository.findByProductId(productId);
    }

    @Override
    public Page<Review> getProductReviews(String productId, Pageable pageable) {
        log.info("Fetching reviews for product: {} with pagination", productId);
        return reviewRepository.findByProductId(productId, pageable);
    }

    @Override
    public List<Review> getUserReviews(String userId) {
        log.info("Fetching reviews by user: {}", userId);
        return reviewRepository.findByUserId(userId);
    }

    @Override
    public Review updateReview(String reviewId, int rating, String comment) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        review.setRating(rating);
        review.setComment(comment);

        Review updated = reviewRepository.save(review);
        
        log.info("Updated review {}", reviewId);
        
        return updated;
    }

    @Override
    public void deleteReview(String reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        
        String productId = review.getProductId();
        String userId = review.getUserId();
        
        reviewRepository.deleteById(reviewId);
        
        log.info("Deleted review {}", reviewId);
    }
}
