package com.inflearn.jpa.api;

import com.inflearn.jpa.domain.Order;
import com.inflearn.jpa.domain.OrderItem;
import com.inflearn.jpa.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class MemberApiControllerTest {

    @Autowired
    OrderRepository orderRepository;

    @Test
    void order() throws Exception {
        List<Order> all = orderRepository.findAll();
        for (Order order : all) {
            System.out.println("order.getMember().getName() = " + order.getMember().getName());
            System.out.println("order.getDelivery().getAddress() = " + order.getDelivery().getAddress());

            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                System.out.println("orderItem.getOrderPrice() = " + orderItem.getOrderPrice());
                System.out.println("orderItem.getItem().getName() = " + orderItem.getItem().getName());
            }
        }
    }

}