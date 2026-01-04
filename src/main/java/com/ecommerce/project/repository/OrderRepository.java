package com.ecommerce.project.repository;

import com.ecommerce.project.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

    List<Order> findByUserId(String userId);

    Page<Order> findByUserId(String userId, Pageable pageable);

    List<Order> findByUserIdOrderByOrderDateDesc(String userId);

    List<Order> findByStatus(Order.Status status);

    // Custom query to find orders with successful payments
    @Query("{ '_id': { $in: ?0 } }")
    List<Order> findByIdIn(List<String> orderIds);

    @Query("{ '_id': { $in: ?0 } }")
    Page<Order> findByIdIn(List<String> orderIds, Pageable pageable);
}
