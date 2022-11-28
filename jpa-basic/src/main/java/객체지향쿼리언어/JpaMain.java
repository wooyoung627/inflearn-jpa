package 객체지향쿼리언어;


import 객체지향쿼리언어.intro.Member;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {



            tx.commit();
        }catch (Exception e){
            tx.rollback();
            e.printStackTrace();
        }finally {
            em.close();
        }

        emf.close();
    }


    // 소개
    /*
            // JPQL : 객체지향 SQL
            // 동적 쿼리 만들기 어려움
            List<Member> result = em.createQuery("select m from  Member m where m.username like '%kim%'", Member.class).getResultList();
            for (Member member : result) {
                System.out.println("member: " + member);
            }

            // Criteria
            // 동적 쿼리 작성이 편리함
            // => 사용 X , 유지보수 어려움
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Member> query = cb.createQuery(Member.class);
            Root<Member> from = query.from(Member.class);

            CriteriaQuery<Member> cq = query.select(from).where(cb.equal(from.get("username"), "kim"));
            List<Member> resultList = em.createQuery(cq).getResultList();

            Member member = new Member();
            member.setUsername("kim");
            em.persist(member);


            // query 날라갈때 flush 동작 => JDBC를 이용하여 직접 쿼리날릴땐 적절한 시점에 Flush를 해주어야 한다.
            // Native Query
            List<Member> resultList = em.createNativeQuery("select id, username from Member", Member.class).getResultList();
            for (Member member1 : resultList) {
                System.out.println("member = " + member1);
            }
*/

}
