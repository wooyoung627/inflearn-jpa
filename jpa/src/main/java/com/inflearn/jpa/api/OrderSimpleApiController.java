package com.inflearn.jpa.api;

import com.inflearn.jpa.domain.Address;
import com.inflearn.jpa.domain.Order;
import com.inflearn.jpa.domain.OrderStatus;
import com.inflearn.jpa.repository.OrderRepository;
import com.inflearn.jpa.repository.OrderSearch;
import com.inflearn.jpa.repository.order.simplequery.OrderSimpleQueryDto;
import com.inflearn.jpa.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne (ManyToOne, OneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    // ** EAGER로는 절대 바꾸어선 안된다. **
    // 1. 무한 루프 발생 -> 양방향 연관관계가 걸린곳 JsonIgnore 처리
    // 2. fetch = LAZY 로 인한 프록시 객체 문제 => Hibernate5Module 빈등록 (필요없는 쿼리도 다 나감)
    // ==> Api에서 Entity 직접 노출 금지
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByJpql(new OrderSearch());
        for (Order order : all) {
            // 3. Lazy 강제 초기화
            order.getMember().getName();
            order.getDelivery().getAddress();
        }
        return all;
    }

    // Lazy 로딩으로 인해 쿼리가 너무 많이 나감
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){
        // N + 1 문제
        // 1 + 회원 N + 배송 N
        List<Order> orders = orderRepository.findAllByJpql(new OrderSearch());

        // 엔티티를 DTO로 변환하는 일반적인 방법
        List<SimpleOrderDto> simpleOrders = orders.stream()
                .map(SimpleOrderDto::new)
                .collect(Collectors.toList());
        return simpleOrders;
    }

    // Fetch Join 페치 조인 사용
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        return orders.stream()
                .map(SimpleOrderDto::new)
                .collect(Collectors.toList());
    }

    // v3와 v4의 차이
    // v3: 모든 컬럼 select -> Order 엔티티 자체를 반환 재사용성이 높음
    // v4: 내가 원하는 컬럼만 select (v3와 성능차이 미비함) -> DTO에 맞는 쿼리라 재사용성이 낮음
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4(){
        return orderSimpleQueryRepository.findOrderDtos();
    }

    /**
     * 쿼리 방식 선택 권장 순서
     *
     * 1. 우선 엔티티를 DTO로 변환하는 방법을 선택한다. (유지보수성에서 좋음)
     * 2. 필요하면 페치 조인으로 성능 최적화 -> 대부분의 성능 이슈 해결 (v3)
     * 3. DTO로 직접 조회하는 방법 사용 (v4)
     * 4. 최후의 방법은 JPA가 제공하는 네이티브 SQL이나 스프링 JDBC Template을 사용해서 SQL 직접 사용
     */


    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order){
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화
        }
    }
}
