package com.ecommerce.project.repository;

import com.ecommerce.project.entity.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {

    List<Review> findByProductId(String productId);

    List<Review> findByUserId(String userId);

    boolean existsByUserIdAndProductId(String userId, String productId);
}
