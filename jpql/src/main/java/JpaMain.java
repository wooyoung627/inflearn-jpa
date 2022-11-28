import jakarta.persistence.*;
import jpql.*;

import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {

            Team teamA = new Team();
            teamA.setName("팀A");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("팀B");
            em.persist(teamB);

            Member member = new Member();
            member.setUsername("회원1");
            member.setTeam(teamA);
            em.persist(member);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.setTeam(teamB);
            em.persist(member3);

//            em.flush();
//            em.clear();

            // 벌크 연산 (쿼리 한 번으로 여러 테이블 로우 변경)
            // 영속성 컨텍스트를 무시하고 DB에 직접 쿼리
            // 1. 벌크연산을 먼저 실행 or 벌크 연산 수행 후 영속성 컨텍스트 초기화
            String query = "update Member m set m.age = 20";

            // flush 자동 호출 (flush는 commit 하거나 query 날리거나 강제 flush 호출시 수행된다)
            int resultCount = em.createQuery(query)
                    .executeUpdate();
            System.out.println("resultCount = " + resultCount);

            // => 영속성 컨텍스트엔 반영이 안 되어 있어서 age 0으로 나옴
            System.out.println("member : " + member);
            System.out.println("member2 : " + member2);
            System.out.println("member3 : " + member3);

            em.clear(); // clear 후 에 find 해야 select query 나감

            tx.commit();
        }catch (Exception e){
            tx.rollback();
            e.printStackTrace();
        }finally {
            em.close();
        }

        emf.close();
    }

    // 기본 문법과 쿼리 API
    /*
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

            TypedQuery<Member> query = em.createQuery("select m from Member m where m.username = :username", Member.class);
            query.getResultList();
            // 결과가 하나가 아니라면 Exception
            // Spring Data JPA는 결과가 없을경우 -> optional 혹은 null 반환
            query.getSingleResult();

            // position과 변수명으로 parameter 넘김 => position은 안쓰는게 좋음
            Member singleResult = em.createQuery("select m from Member m where m.username = :username", Member.class)
                    .setParameter("username", "member1")
                    .getSingleResult();
            System.out.println("singleResult = " + singleResult);*/

    // 프로젝션
    /*            // 리스트에 있는 Member들 모두 영속되어 있음
            // 엔티티 프로젝션
            List<Member> result = em.createQuery("select m from Member m", Member.class)
                    .getResultList();
            Member member1 = result.get(0);
            member1.setAge(20);

            // 임베디드 타입 프로젝션
            em.createQuery("select o.address from Order o", Address.class)
                    .getResultList();

            // 스칼라 타입 프로젝션
            // Object[]
            List<Object[]> resultList = em.createQuery("select distinct m.username, m.age from Member m")
                    .getResultList();

            Object[] result = resultList.get(0);
            System.out.println("username = " + result[0]);
            System.out.println("age = " + result[1]);

            // new 명령어로 조회
            // 패키지 명을 포함한 전체 클래스 명 입력, 순서와 타입이 일치하는 생성자 필요
            List<MemberDTO> resultList = em.createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                    .getResultList();

            MemberDTO memberDTO = resultList.get(0);
            System.out.println("memberDTO = " + memberDTO);*/

    // 페이징
    /*            for(int i=0; i<100; i++){
                Member member = new Member();
                member.setUsername("member" + i);
                member.setAge(i);
                em.persist(member);
            }

            em.flush();
            em.clear();

            // 페이징
            List<Member> result = em.createQuery("select m from Member m order by m.age desc", Member.class)
                    .setFirstResult(10)
                    .setMaxResults(10)
                    .getResultList();

            System.out.println("result.size = " + result.size());
            for (Member member1 : result) {
                System.out.println("member = " + member1);
            }*/

    // 조인
    /*            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("teamA");
            member.setAge(10);
            member.setTeam(team);

            em.persist(member);

            em.flush();
            em.clear();

//            String query = "select m from Member m inner join m.team t";
//            String query = "select m from Member m left outer join m.team t";
            // cross join
//            String query = "select m from Member m, Team t where m.username = t.name";
            // 연관관계 조인
//            String query = "select m from Member m left join m.team t on t.name = 'teamA'";
            // 연관관계 없는 객체들의 조인
            String query = "select m from Member m left join Team t on m.username = t.name";

            // 페이징
            List<Member> result = em.createQuery(query, Member.class)
                    .getResultList();
            System.out.println("result size : " + result.size());*/

    // 서브쿼리
    /*            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("teamA");
            member.setAge(10);
            member.setTeam(team);

            em.persist(member);

            em.flush();
            em.clear();

            // from 절에선 서브쿼리 사용 불가 (select mm from (select m.age, m.username from Member m) mm)
            // => 1. 조인으로 풀 수 있으면 풀어서 해결, 2. 쿼리 두번 날려서 해결, 3. native
            String query = "select (select avg(m1.age) from Member m1) as avgAge from Member m left join Team t on m.username = t.name";
            List<Member> result = em.createQuery(query, Member.class)
                    .getResultList();
            System.out.println("result size : " + result.size());*/

    // JPQL 타입 표현과 기타식
    /*            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("teamA");
            member.setAge(10);
            member.setTeam(team);
            member.setType(MemberType.ADMIN);

            em.persist(member);

            em.flush();
            em.clear();

            // enum
//            String query = "select m.username, 'HELLO', true from Member m where m.type = jpql.MemberType.USER";
            String query = "select m.username, 'HELLO', true from Member m where m.type = :userType";
            List<Object[]> result = em.createQuery(query)
                    .setParameter("userType", MemberType.USER)
                    .getResultList();
            for (Object[] objects : result) {
                System.out.println("objects = " + objects[0]);
                System.out.println("objects = " + objects[1]);
                System.out.println("objects = " + objects[2]);
            }*/

    // 조건식
    /*            String query = "select " +
                    "case when m.age <= 10 then '학생요금' " +
                    "     when m.age >= 60 then '경로요금' " +
                    "     else '일반요금' " +
                    "end " +
                    "from Member m";

            List<String> resultList = em.createQuery(query, String.class).getResultList();
            for (String s : resultList) {
                System.out.println("s = " + s);
            }

            String query = "select coalesce(m.username, '이름 없는 회원') from Member m";
            List<String> resultList = em.createQuery(query, String.class).getResultList();
            for (String s : resultList) {
                System.out.println("s = " + s);
            }

            // nullif : 값이 같으면 null 다르면 첫번째 값 반환
            String query = "select nullif(m.username, '관리자') from Member m";
            List<String> resultList = em.createQuery(query, String.class).getResultList();
            for (String s : resultList) {
                System.out.println("s = " + s);
            }*/

    // JPQL 함수
    /*//            String query = "select concat('a', 'b')"; // => ab
//            String query = "select substring(m.username, 2, 3) from Member m";
//            String query = "select locate('dd', 'aabbccddee') from Member m"; // => 7
//            String query = "select size(t.members) from Team t"; // => 1 team의 멤버 크기를 돌려줌
            String query = "select size(t.members) from Team t";

            List<Integer> resultList = em.createQuery(query, Integer.class).getResultList();
            for (Integer s : resultList) {
                System.out.println("s = " + s);
            }*/


    // 경로 표현식
    /*            // 단일 값 연관 경로
            // 묵시적 내부 조인(inner join) 발생 => 이런식으로 쿼리를 작성하면 안됨, 쿼리 튜닝이 어려워짐
//            String query = "select m.team from Member m"; // => select m from Member m join m.team t

            // 컬렉션 값 연관 경로
            // 묵시적 내부조인 발생
            // 탐색 X (t.members.name 안됨) => 명시적 조인을 통해 별칭을 얻어 탐색
//            String query = "select t.members from Team t";
            // 명시적 조인
//            String query = "select m.username from Team t join t.members m";

            // ** 묵시적 조인은 쓰지 않는게 좋다 **

            String query = "select m from Member m";

            List<Member> resultList = em.createQuery(query, Member.class).getResultList();
            for (Member member1 : resultList) {
                System.out.println("member1 = " + member1);
            }*/

    // 페치 조인1 - 기본
    /*           Team teamA = new Team();
            teamA.setName("팀A");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("팀B");
            em.persist(teamB);

            Member member = new Member();
            member.setUsername("회원1");
            member.setTeam(teamA);
            em.persist(member);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.setTeam(teamB);
            em.persist(member3);

            em.flush();
            em.clear();

            String query = "select m from Member m";

            List<Member> resultList = em.createQuery(query, Member.class).getResultList();
            for (Member member1 : resultList) {
                System.out.println("member1 = " + member1.getUsername() + ", " + member1.getTeam().getName());
                // 지연 로딩 설정시 getName했을때 team을 select 해온다.
                // 1(Member select) + N(회원 100명) 문제 발생
                // => fetch join으로 해결
            }


//             엔티티 페치 조인 : 회원을 조회하면서 연관된 팀도 함께 조회 (query가 1번 나감)
//             즉시 로딩과 같다(= EAGER)
            String query = "select m from Member m join fetch m.team";
            List<Member> resultList = em.createQuery(query, Member.class).getResultList();
            for (Member member1 : resultList) {
                // join으로 한번에 select 날라감
                System.out.println("member1 = " + member1.getUsername() + ", " + member1.getTeam().getName());
            }

            // 중복으로 나옴 팀A에 회원이 2명이라 팀A가 두번나옴 => distinct 추가 (컬렉션에서 중복을 없애줌)
            // 일대다 관계는 데이터가 뻥튀기 될 수 있음
            String query = "select distinct t From Team t join fetch t.members";
            List<Team> resultList = em.createQuery(query, Team.class).getResultList();
            System.out.println("result size : " + resultList.size());
            for (Team team : resultList) {
                System.out.println("team = " + team.getName());
                for(Member m : team.getMembers())
                    System.out.println("member = " + m);
            }*/

    // 페치조인 - 한계
    /*            select
                t1_0.team_id,
                t1_0.name
            from
                Team t1_0

            // batch size 만큼 in(size) 해서 팀에 관련된 멤버를 모두 가져옴
            // LAZY 로딩 가져올 때 한번에 batch size 팀의 멤버를 모두 GET
             select
                m1_0.team_id,
                m1_0.member_id,
                m1_0.age,
                m1_0.type,
                m1_0.username
            from
                Member m1_0
            where
                m1_0.team_id in(?,?) // batch size만큼의 team


            String query = "select t from Team t";
            List<Team> resultList = em.createQuery(query, Team.class).getResultList();
            for (Team team : resultList) {
                System.out.println("team = " + team);
                for(Member m:team.getMembers())
                    System.out.println("m = " + m);
            }*/

    // 엔티티 직접 사용
    /*            // count(m) 처럼 엔티티를 직접 넘기면 m의 기본키 값을 사용한다.
//            String query = "select count(m) from Member m";

//            List<Long> resultList = em.createQuery(query, Long.class).getResultList();
//            for (Long integer : resultList) {
//                System.out.println("integer = " + integer);
//            }

//            String query = "select m from Member m where m = :member"; // 알아서 id로 select
            String query = "select m from Member m where m.team = :team";
            List<Member> resultList = em.createQuery(query, Member.class)
                    .setParameter("team", teamA)
                    .getResultList();
            for (Member member1 : resultList) {
                System.out.println("member1 = " + member1);
            }*/

    // Named 쿼리
    /*
                List<Member> resultList = em.createNamedQuery("Member.findByUsername", Member.class)
                    .setParameter("username", "회원1")
                    .getResultList();
            for (Member member1 : resultList) {
                System.out.println("member1 = " + member1);
            }
    */
}
