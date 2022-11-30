package com.inflearn.jpa.repository;

import com.inflearn.jpa.domain.*;
import com.inflearn.jpa.domain.Order;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static com.inflearn.jpa.domain.QMember.*;
import static com.inflearn.jpa.domain.QOrder.*;

// repository는 순수한 entity를 조회하는데 쓴다.
@Repository
public class OrderRepository {

    private final EntityManager em;
    private final JPAQueryFactory query;

    public OrderRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }


    /**
     * Querydsl
     */
//    public List<Order> findAll(OrderSearch orderSearch){
//    }

    /**
     * JPQL 쿼리를 문자로 생성하기는 번거롭고, 실수로 인한 버그가 충분히 발생할 수 있다.
     */
    public List<Order> findAllByJpql(OrderSearch orderSearch){
        String jpql = "select o from Order o join o.member m";

        boolean isFirstCondition = true;

        //주문 상태 검색
        if(orderSearch.getOrderStatus() != null){
            if(isFirstCondition){
                jpql += " where";
                isFirstCondition = false;
            } else{
                jpql = " and";
            }
            jpql += " o.status = :status";
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();


        // 동적 쿼리를 사용해야함 (status, name 없을 경우) => 매우 안좋음 jpql 수동으로 작성, parameter 수동 작성
//        return em.createQuery("select o from Order o join o.member m " +
//                "where o.status = :status " +
//                "and m.name like :name", Order.class)
//                .setParameter("status", orderSearch.getOrderStatus())
//                .setParameter("name", orderSearch.getMemberName())
//                .getResultList();
    }

    /**
     * JPA Criteria => 권장 X, jpa로 동적쿼리를 작성할때 사용
     */
    public List<Order> findAllByCriteria(OrderSearch orderSearch){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
        List<Predicate> criteria = new ArrayList<>();

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건
        return query.getResultList();
    }

    public List<Order> findAll(){
//        return em.createQuery("select o from Order o", Order.class).getResultList();
        return em.createQuery("select o from Order o join fetch o.member m join fetch o.delivery d", Order.class).getResultList();
    }

    public List<Order> findAll(OrderSearch orderSearch) {
        return query
                .select(order)
                .from(order)
                .join(order.member, member)
                .where(statusEq(orderSearch.getOrderStatus()),
                        nameLike(orderSearch.getMemberName()))
                .limit(1000)
                .fetch();
    }

    private BooleanExpression nameLike(String memberName){
        if(!StringUtils.hasText(memberName))
            return null;
        else return member.name.like(memberName);
    }

    private BooleanExpression statusEq(OrderStatus statusCond){
        if(statusCond == null){
            return null;
        }else return order.status.eq(statusCond);
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
        ).getResultList();
    }

    /**
     * ToOne 관계를 모두 페치조인 (row수를 증가시키지 않으므로 페이징 쿼리에 영향을 주지 않는다.)
     * 컬렉션은 지연 로딩으로 조회
     * 지연 로딩 성능 최적화를 위해 hibernate.default_batch_fetch_size(추천), @BatchSize 적용
     * default_batch_fetch_size
     * => in 쿼리로 컬렉션의 데이터를 미리 가져옴
     */
    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
                ).setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }


/*    public List<OrderSimpleQueryDto> findOrderDtos() {
        // new 명령어를 사용해서 JPQL의 결과를 DTO로 즉시 변환
        // 리포지토리 재사용성이 떨어지고 API 스펙에 맞춘 코드가 리포지토리에 들어가는 단점이 있음
        return em.createQuery(
                "select new com.inflearn.jpa.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }*/

    public List<Order> findAllWithItem() {
        // 데이터가 뻥튀기 돼서 나옴-> join 된 데이터 모두 출력
        /*
        return em.createQuery(
                "select o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d" +
                " join fetch o.orderItems oi" +
                " join fetch oi.item i", Order.class)
                .getResultList();
        */

        // distinct : sql에 distinct를 추가할뿐만 아니라 JPA에서 가져올때 Order가 같은값이면 중복을 걸러준다.
        // 단점 : collection fetch 일시 페이징 불가능 (메모리에서 페이징 작업)
        // 컬렉션 둘 이상에 페치조인 사용 X
        return em.createQuery(
                "select distinct o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d" +
                " join fetch o.orderItems oi" +
                " join fetch oi.item i", Order.class)
                .getResultList();
    }

}
