package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

/*
사용자 정의 리포지토리 구현 - 구현체 클래스
명명규칙 : 해당 클래스를 상속할 spring data jpa 인터페이스 명 + Impl
명명 규칙을 반드시 지켜야 Spring Data Jpa가 정상적으로 조립해준다.
 */
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final EntityManager em;
    @Override
    public List<Member> findMeberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
