package com.inflearn.jpa.service;

import com.inflearn.jpa.domain.Member;
import com.inflearn.jpa.repository.MemberRepositoryOld;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepositoryOld memberRepository;
    @Autowired
    EntityManager em;

    @Test
    void 회원가입() throws Exception {
        // given
        Member member = new Member();
        member.setName("lee2");


        // when
        Long savedId = memberService.join(member);

//        em.flush(); // 명령을 DB에 반영 insert query 나감
        // then
        assertEquals(member, memberRepository.findOne(savedId));
    }

    @Test
    void 중복_회원_예외() throws Exception {
        // given
        Member member1 = new Member();
        member1.setName("lee");

        Member member2 = new Member();
        member2.setName("lee");

        // when
        memberService.join(member1);
//        try {

        assertThrows(IllegalStateException.class, () -> {
            memberService.join(member2);
        });
//        }catch (IllegalStateException e){
//            return;
//        }

        // then
//        fail("예외가 발생해야 한다.");
    }

}