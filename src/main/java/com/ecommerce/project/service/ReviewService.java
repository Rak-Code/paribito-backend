package com.ecommerce.project.service;

import com.ecommerce.project.dto.ReviewRequestDTO;
import com.ecommerce.project.entity.Review;
import java.util.List;

public interface ReviewService {

    Review addReview(ReviewRequestDTO dto);

    Review getReview(String reviewId);

    List<Review> getProductReviews(String productId);

    List<Review> getUserReviews(String userId);

    Review updateReview(String reviewId, int rating, String comment);

    void deleteReview(String reviewId);
}
