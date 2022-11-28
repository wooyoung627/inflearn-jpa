package 값타입;


import 값타입.collection.Address;
import 값타입.collection.AddressEntity;
import 값타입.collection.Member;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class ValueMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {

            Member member = new Member();
            member.setUsername("member1");
            member.setHomeAddress(new Address("homeCity", "street1", "10000"));

            member.getFavoriteFoods().add("치킨");
            member.getFavoriteFoods().add("족발");
            member.getFavoriteFoods().add("피자");

            member.getAddressHistory().add(new AddressEntity("old1", "street1", "10000"));
            member.getAddressHistory().add(new AddressEntity("old2", "street1", "10000"));

            // 값 타입 컬렉션들도 자동으로 persist 됨
            em.persist(member);

            em.flush();
            em.clear();

/*            // 값 타입 조회
            System.out.println("=========================================");
            // member select query만 나감 (컬렉션들은 지연로딩)
            Member findMember = em.find(Member.class, member.getId());

            // 이때 query 나가는게 아닌
            List<Address> addressHistory = findMember.getAddressHistory();
            System.out.println("=========================================");

            // 직접 값을 가져올때 query가 나감
            for (Address address : addressHistory) {
                System.out.println("address : " + address.getCity());
            }

            Set<String> favoriteFoods = findMember.getFavoriteFoods();
            System.out.println("=========================================");
            for (String favoriteFood : favoriteFoods) {
                System.out.println("food : " + favoriteFood);
            }*/

           /* // 값 타입 수정
            Member findMember = em.find(Member.class, member.getId());

            // homeCity -> newCity
            // findMember.getHomeAddress().setCity("newCity"); (X)
            Address a = findMember.getHomeAddress();
            findMember.setHomeAddress(new Address("newCity", a.getStreet(), a.getZipcode()));

            // 치킨 -> 한식
            // 값타입 컬렉션은 업데이트가 아닌 통째로 갈아끼워야 한다
            findMember.getFavoriteFoods().remove("치킨");
            findMember.getFavoriteFoods().add("한식");

            // 여기서 equals 함수로 찾음
            // member_id로 table의 데이터 모두 delete 한 다음 컬렉션 값 모두 insert => 값타입 컬렉션은 추천 X
            // 값 타입 컬렉션 대신 일대다 관계를 고려, 값타입을 엔티티로 승급
            findMember.getAddressHistory().remove(new AddressEntity("old1", "street1", "10000"));
            findMember.getAddressHistory().add(new AddressEntity("newCity1", "street1", "10000"));*/

            Member findMember = em.find(Member.class, member.getId());

            tx.commit();
        }catch (Exception e){
            tx.rollback();
            e.printStackTrace();
        }finally {
            em.close();
        }

        emf.close();
    }

    // 값 타입과 불변 객체
    /*            Address address = new Address("city","street","10000");

            Member member = new Member();
            member.setUsername("member1");
            member.setHomeAddress(address);
//            member.setPeriod(new Period(LocalDateTime.now(), LocalDateTime.now()));
            em.persist(member);

            Address copyAddress = new Address(address.getCity(), address.getStreet(), address.getZipcode());

            Member member2 = new Member();
            member2.setUsername("member2");
            member2.setHomeAddress(copyAddress);
            em.persist(member2);

            member.getHomeAddress().setCity("newCity");*/

}
