package com.ecommerce.project.repository;

import com.ecommerce.project.entity.BespokeOrder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BespokeOrderRepository extends MongoRepository<BespokeOrder, String> {
    
    List<BespokeOrder> findByUserId(String userId);
    
    List<BespokeOrder> findByStatus(BespokeOrder.OrderStatus status);
    
    List<BespokeOrder> findByUserIdAndStatus(String userId, BespokeOrder.OrderStatus status);
}
