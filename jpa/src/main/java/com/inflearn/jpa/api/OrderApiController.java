package com.inflearn.jpa.api;

import com.inflearn.jpa.domain.Address;
import com.inflearn.jpa.domain.Order;
import com.inflearn.jpa.domain.OrderItem;
import com.inflearn.jpa.domain.OrderStatus;
import com.inflearn.jpa.repository.OrderRepository;
import com.inflearn.jpa.repository.OrderSearch;
import com.inflearn.jpa.repository.order.query.OrderFlatDto;
import com.inflearn.jpa.repository.order.query.OrderItemQueryDto;
import com.inflearn.jpa.repository.order.query.OrderQueryDto;
import com.inflearn.jpa.repository.order.query.OrderQueryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    /**
     * 권장 순서
     * 1. 엔티티 조회 방식으로 우선 접근
     * 1-1) 페치조인으로 쿼리 수를 최적화
     * 1-2) 컬렉션 최적화
     * 1-2-1) 페이징 필요 - hibernate.default_batch_fetch_size, @BatchSize
     * 1-2-2) 페이징 필요 X - 컬렉션까지 페치 조인 사용
     * 2. 엔티티 조회 방식으로 해결이 안되면 DTO 조회 방식 사용
     * 3. DTO 조회 방식으로 해결이 안되면 NativeSQL or Spring JdbcTemplate
     */


    // 엔티티를 조회해서 그대로 반환
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByJpql(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.forEach(orderItem -> orderItem.getItem().getName());
        }

        return all;
    }

    // 엔티티 조회 후 DTO로 변환
    // 쿼리가 상당히 많이 실행됨
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByJpql(new OrderSearch());
        return orders.stream().map(OrderDto::new)
                .collect(toList());
    }


    // 페치 조인으로 쿼리 수 최적화
    // 쿼리 1번으로 데이터를 가져옴 => DB에서 애플리케이션으로 데이터를 다 전송함
    // ** 컬렉션은 페치 조인시 페이징 불가능 **
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithItem();

        return orders.stream()
                .map(OrderDto::new)
                .collect(toList());
    }

    // 컬렉션 페이징과 한계 돌파
    // 쿼리는 여러번(1+1(order item)+1(item)) 나가지만 뻥튀기된 데이터가 아니라 최적화된 데이터(중복없는 정규화된 데이터)가 나온다.
    // ToOne 관계는 페치 조인으로 가져오는게 나음
    // ** 페이징 가능 **
    // default_batch_fetch_size는 맥시멈 1000개로 잡는게 좋다. (100~1000 권장)
    // 메모리는 옵션과 관계없이 전체 데이터를 로딩하기 때문에 메모리 사용량은 같다.
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                        @RequestParam(value = "limit", defaultValue = "100") int limit){
        // ToOne Fetch Join
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        return orders.stream()
                .map(OrderDto::new)
                .collect(toList());
    }


    // JPA에서 DTO를 직접 조회
    // 쿼리 1+N번 (직접 for문 돌며 orderItems 초기화)
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    // 컬렉션 조회 최적화 - 일대다 관계인 컬렉션은 IN 절을 활용해서 메모리에 미리 조회해서 최적화
    // 쿼리 2번 (findOrderItemMap)
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }

    // 플랫 데이터 최적화 - JOIN 결과를 그대로 조회 후 애플리케이션에서 원하는 모양으로 직접 변환
    // 장점 : 쿼리 1번
    // 단점
    // 중복데이터가 추가되므로 상황에 따라 V5보다 느릴 수 있음
    // 애플리케이션에서 추가 작업이 필요, 페이징 불가능
    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6() {
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();
        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
                                o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                                o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),e.getKey().getAddress(), e.getValue()))
                .collect(toList());
    }

    @Getter
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems()
                    .stream()
                    .map(OrderItemDto::new)
                    .collect(toList());
        }
    }

    // OrderItem도 엔티티이니 외부에 노출하지 말 것
    @Getter
    static class OrderItemDto {

        private String itemName; // 상품 명
        private int orderPrice; // 주문 가격
        private int count; // 주문 수량

        public OrderItemDto(OrderItem orderItem){
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }

}
