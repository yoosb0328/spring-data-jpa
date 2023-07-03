package study.datajpa.repository;

import study.datajpa.entity.Member;

import java.util.List;
/*
사용자 정의 리포지토리 구현 - 인터페이스
-> 구현체 구현 후 스프링 데이터 jpa 리포지토리에 extends 할 수 있다.
 */
public interface MemberRepositoryCustom {
    List<Member> findMeberCustom();
}
