package com.inflearn.jpa.service;

import com.inflearn.jpa.repository.OrderSearch;
import com.inflearn.jpa.domain.Delivery;
import com.inflearn.jpa.domain.Order;
import com.inflearn.jpa.domain.OrderItem;
import com.inflearn.jpa.domain.item.Item;
import com.inflearn.jpa.domain.Member;
import com.inflearn.jpa.repository.ItemRepository;
import com.inflearn.jpa.repository.MemberRepositoryOld;
import com.inflearn.jpa.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepositoryOld memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count){

        // transaction 상태에서 핵심 비즈니스 로직을 실행해야 영속성 컨텍스트 상태에서 조회할 수 있음
        // 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        orderRepository.save(order);
        return order.getId();
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId){
        // 주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        // 주문 취소
        order.cancel();
        // JPA를 사용할 경우 데이터에 변경이 발생하면 자동으로 update 쿼리가 날라간다.
    }

    /**
     * 주문 검색
     * 단순하게 화면 조회를 위한 기능이라면 컨트롤러에서 호출해도 o (단순 위임이라면)
     */
    public List<Order> findOrders(OrderSearch orderSearch){
        return orderRepository.findAll(orderSearch);
    }

}
