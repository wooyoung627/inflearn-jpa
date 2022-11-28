package com.inflearn.jpa.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

// query용 repository 생성
@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    public List<OrderSimpleQueryDto> findOrderDtos() {
        // new 명령어를 사용해서 JPQL의 결과를 DTO로 즉시 변환
        // 리포지토리 재사용성이 떨어지고 API 스펙에 맞춘 코드가 리포지토리에 들어가는 단점이 있음
        return em.createQuery(
                        "select new com.inflearn.jpa.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) from Order o" +
                                " join o.member m" +
                                " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }

}
