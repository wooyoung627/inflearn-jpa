package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * 인터페이스 만으로 해결이 안될 때 직접 구현해서 사용
 * 그러나 화면에 맞춘 DTO를 return하는 등의 로직은 MemberQueryRepository같이 Component로 직접 등록해서 사용하는게 낫다(분리)
 * MemberRepository가 findMemberCustom 사용
 * 예) QueryDsl
 *
 * 이름을 -Impl로 지어야 함
 */
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
}
