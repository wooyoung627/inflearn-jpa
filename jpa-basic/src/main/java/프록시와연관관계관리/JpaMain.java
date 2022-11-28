package 프록시와연관관계관리;

import org.hibernate.Hibernate;
import org.hibernate.jpa.internal.PersistenceUnitUtilImpl;
import 상속관계매핑.mapping.Movie;
import 프록시와연관관계관리.cascade.Child;
import 프록시와연관관계관리.cascade.Parent;
import 프록시와연관관계관리.proxy.Member;
import 프록시와연관관계관리.proxy.Team;

import javax.persistence.*;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            Child child1 = new Child();
            Child child2 = new Child();

            Parent parent = new Parent();
            parent.addChild(child1);
            parent.addChild(child2);

            em.persist(parent); // cascade all 일 경우 parent만 persist 날려도 childList 알아서 persist
//            em.persist(child1);
//            em.persist(child2);

            em.flush();
            em.clear();

            Parent findParent = em.find(Parent.class, parent.getId());
            // orphanRemoval = true 일 경우 알아서 delete query 날려줌
//            findParent.getChildList().remove(0);

            em.remove(findParent);

            tx.commit();
        }catch (Exception e){
            tx.rollback();
            e.printStackTrace();
        }finally {
            em.close();
        }

        emf.close();
    }

    private static void print(String test) {
        System.out.println(test);
    }

    // 프록시
            /*Member member = new Member();
            member.setUsername("hello");
            em.persist(member);

            em.flush();
            em.clear();

            // 1
            // getReference를 호출할땐 쿼리 x
            Member findMember = em.getReference(Member.class, member.getId());
            System.out.println("findMember id : " + findMember.getId());
            // 이때 쿼리가 나감 (username은 엔티티 영속성 안에 없기 때문)
            System.out.println("findMember username : " + findMember.getUsername());


            // 2
            Member member1 = em.find(Member.class, member.getId());
            Member refer = em.getReference(Member.class, member.getId());

            // 이미 영속성 컨텍스트에 있다면 Member 객체 반환
            System.out.println("member1 getClass : " + member1.getClass());
            System.out.println("refer getClass : " + refer.getClass());


            // 3
            Member m1 = em.getReference(Member.class, member.getId());
            Member refer = em.getReference(Member.class, member.getId());

            System.out.println("m1 getClass : " + m1.getClass());
            System.out.println("refer getClass : " + refer.getClass());
            System.out.println("m1 == refer : " + (m1 == refer)); // true


            // 4
            Member refer = em.getReference(Member.class, member.getId());
            System.out.println("refer getClass : " + refer.getClass()); // proxy

            // select query
            Member find = em.find(Member.class, member.getId());
            System.out.println("find getClass : " + find.getClass()); // proxy (proxy로 한번 조회했다면 proxy 객체 반환)

            System.out.println("refer == find : " + (refer == find)); // true

            Member refer = em.getReference(Member.class, member.getId());
            System.out.println("ref : " + refer.getClass());
//            refer.getUsername();

//            em.detach(refer);
            // or em.clear();

            // org.hibernate.LazyInitializationException : could not initialize proxy (영속성 컨텍스트에 존재하지 않음)
//            System.out.println("username : " + refer.getUsername());


            // 5
            // proxy 객체가 초기화 되었나
            System.out.println("isLoaded : " + emf.getPersistenceUnitUtil().isLoaded(refer));

            // 프록시 클래스 확인
            System.out.println("proxy class : " + refer.getClass().getName());

            // 프록시 강제 초기화 (강제 쿼리 날림)
            Hibernate.initialize(refer);*/




    // 즉시 로딩과 지연 로딩
    /* Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Team teamB = new Team();
            teamB.setName("teamB");
            em.persist(teamB);

            Member member = new Member();
            member.setUsername("name");
            member.setTeam(team);
            em.persist(member);

            Member member2 = new Member();
            member2.setUsername("name2");
            member2.setTeam(teamB);
            em.persist(member2);

            em.flush();
            em.clear();

            // 지연로딩 @ManyToOne(fetch = FetchType.LAZY)
            Member find = em.find(Member.class, member.getId());
            // proxy로 가져옴
            System.out.println("find.getTeam: " + find.getTeam().getClass()); // Proxy

            System.out.println("=============");
            // query 나감 , 초기화
            find.getTeam().getName();
            System.out.println("=============");

            // 즉시로딩 @ManyToOne(fetch = FetchType.EAGER)
            Member find = em.find(Member.class, member.getId()); // member join team
            System.out.println("find.getTeam: " + find.getTeam().getClass()); // Team 객체

            System.out.println("=============");
            System.out.println(find.getTeam().getName());
            System.out.println("=============");

            // ** 가급적 지연 로딩만 사용 **
            // XToOne은 기본이 EAGER
            // XTOMany는 기본이 LAZY

            // JPQL 사용시 N+1 문제 발생
            // em.find시 jpa가 내부적으로 최적화를 할수 있음
            // 그러나 jpql은 sql로 번역을 하기 때문에 member를 먼저 가져오고 member 객체안의 team을 뒤늦게 가져온다(+1).
            List<Member> members = em.createQuery("select m from Member m join fetch m.team", Member.class).getResultList();*/
}
