package 연관관계매핑;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {

    /**
     * 영속성 관리 - 내부 동작 방식
     */
    public static void main1(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            // 저장
/*            Member member = new Member();
            member.setId(2L);
            member.setName("helloB");
            // => 비영속

            em.persist(member);
            // => 영속*/

            // 수정
/*            Member findMember = em.find(Member.class, 1L);
            findMember.setName("HelloJPA");*/

            // JPQL
/*            List<Member> result = em.createQuery("select m from Member as m", Member.class)
                    .getResultList();

            for(Member member : result)
                System.out.println("member.name : " + member.getName());*/

            // 비영속
/*            Member member = new Member();
            member.setId(101L);
            member.setName("HelloJPA");

            // 영속
            System.out.println("=== BEFORE ===");
            em.persist(member);
            System.out.println("=== AFTER ===");*/

/*            Member findMember = em.find(Member.class, 101L);
            Member findMember2 = em.find(Member.class, 101L);

            // true, 영속 엔티티의 동일성 보장
            System.out.println("result : " + (findMember == findMember2));*/


/*            Member member1 = new Member(150L, "A");
            Member member2 = new Member(160L, "B");

            // 이 순간에 DB에 저장되는 것이 아님
            em.persist(member1);
            em.persist(member2);
            */


/*            // 엔티티 수정
            Member member = em.find(Member.class, 150L);
            member.setName("ZZZZZ");*/

/*            Member member = new Member(200L, "member200");
            em.persist(member);
            // 영속성 컨텍스트의 변경 내용을 데이터베이스에 반영
            em.flush();*/

/*            Member member = em.find(Member.class, 200L);
            member.setName("member200200");

            // 영속성 컨텍스트에서 더 이상 관리하지 않겠다.
            // 준영속 상태로 전환
            em.detach(member);

            // 영속성 컨텍스트 초기화
            em.clear();

            System.out.println("==============");*/
            // commit 시점에 영속 상태의 엔티티가 디비로 저장됨

            tx.commit();
        }catch (Exception e){
            tx.rollback();
        }finally {
            em.close();
        }

        emf.close();
    }

    /**
     * 엔티티 매핑
     */
    public static void main2(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {

            /*Member member = new Member();
            member.setId(2L);
            member.setUsername("A");
            member.setRoleType(RoleType.USER);
            member.setLocalDate(LocalDate.now());
            member.setLocalDateTime(LocalDateTime.now());*/

            Member2 member = new Member2();
            member.setUsername("NAME");

            // GenerationType.IDENTITY 시 tx.commit에서 실행되는게 아닌 바로 insert 쿼리가 실행됨
            em.persist(member);
            System.out.println("===================================");
            tx.commit();
        }catch (Exception e){
            tx.rollback();
        }finally {
            em.close();
        }

        emf.close();
    }

    /**
     * 연관관계 매핑 기초
     */
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            // 저장
            Team2 team = new Team2();
            team.setName("TeamA");
            em.persist(team);

            Member2 member = new Member2();
            member.setUsername("member1");
            member.changeTeam(team);
            em.persist(member);

            em.flush();
            em.clear();


            // 조회
            Member2 findMember = em.find(Member2.class, member.getId());
            List<Member2> members = findMember.getTeam().getMembers();
            for(Member2 member2 : members){
                System.out.println("member = " + member.getUsername());
            }

            tx.commit();
        }catch (Exception e){
            tx.rollback();
        }finally {
            em.close();
        }

        emf.close();
    }
}
