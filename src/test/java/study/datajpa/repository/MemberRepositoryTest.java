package study.datajpa.repository;

import org.apache.catalina.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext EntityManager em;

    @Test
    public void testMember() {
        //given
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        //when
        Member findMember = memberRepository.findById(savedMember.getId()).get();
        //then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);

    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testFindUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
//        for(String username : usernameList) {
//            System.out.println("username : " + username);
//        }
        assertThat(usernameList.get(0)).isEqualTo(m1.getUsername());
        assertThat(usernameList.get(1)).isEqualTo(m2.getUsername());
    }

    @Test
    public void testMemberDto() {
        Member m1 = new Member("AAA", 10);
        Team team = new Team("TeamA");
        m1.changeTeam(team);

        memberRepository.save(m1);
//        teamRepository.save(team); //member를 부모로 CASCADE.PERSIST 옵션 주었음.

        List<MemberDto> findMemberDto = memberRepository.findMemberDto();
        for(MemberDto memberDto : findMemberDto) {
            System.out.println("dto : " + memberDto);
        }
    }

    @Test
    public void testFindByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> members = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for(Member member : members) {
            System.out.println("Member.username : " + member.getUsername() );
        }
        assertThat(members.size()).isEqualTo(2);
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        Member m3 = new Member("BBB", 30);

        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);


//        Member member1 = memberRepository.findMemberByUsername("adasdasd");// null
//        Member member2 = memberRepository.findOptionalByUsername("asdasd").get();//java.util.NoSuchElementException: No value present
//        Member member3 = memberRepository.findOptionalByUsername("asdasd");//Optional.Empty
//        Member member4 = memberRepository.findMemberByUsername("BBB");//단건조회시 결과 두 건이상 : javax.persistence.NonUniqueResultException
//        Member member5 = memberRepository.findOptionalByUsername("BBB").get();//단건조회시 결과 두 건이상 : javax.persistence.NonUniqueResultException
        List<Member> members = memberRepository.findListByUsername("asdasdad"); // 빈 컬렉션
    }

    @Test
    public void paging() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));//offset 0부터 시작함.
        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        /*
        실무에서 활용한다면 반드시 DTO로 변환해서 리턴한다. 엔티티를 노출하면 안됨.
         */
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), member.getTeam().getName()));

        //Slice<Member> page = memberRepository.findByAge(age, pageRequest);

        //then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

//        for(Member member : content) {
//            System.out.println("member = " + member);
//        }
//        System.out.println("totalElements = " + totalElements);
        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); //총 페이지 수 = 전체count / offset = 2
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void testBulkUpdate() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        int resultCount = memberRepository.bulkAgePlus(20);

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("before 영속성 컨텍스트 초기화 member5 = " + member5);// age = 40이다.
        /*
        벌크 연산을 하게 되면 DB에 업데이트를 일괄적으로 수행하고 영속성 컨텍스트에는 반영이 안됨.
        따라서 영속성 컨텍스트에 있는 member5의 age는 여전히 40이다.
        따라서 벌크 연산 이후 api가 종료되지 않고 로직이 이어진다면 영속성 컨텍스트를 초기화하여야 이러한 문제를 방지할 수 있다.
        단 스프링 데이터 JPA는 벌크연산 메서드에 @Modifying(clearAutomatically = true) 옵션을 설정함으로써 컨텍스트 초기화를 자동화 할 수 있다.
         */

        em.flush();
        em.clear();

        result = memberRepository.findByUsername("member5");
        member5 = result.get(0);
        System.out.println("after 영속성 컨텍스트 초기화 member5 = " + member5);// age = 41이다.

        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();
        //when
        List<Member> members = memberRepository.findAll(); // select Member

        for(Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.team.class = " + member.getTeam().getClass()); //proxy
            System.out.println("member.team = " + member.getTeam().getName()); //select team where id = ?
        }
    }

    @Test
    public void findMemberFetchJoin() {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();
        //when
        List<Member> members = memberRepository.findMemberFetchJoin(); // select Member left join Team

        for(Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.team.class = " + member.getTeam().getClass()); //Team
            System.out.println("member.team = " + member.getTeam().getName()); //쿼리X
        }
    }

    @Test
    public void queryHint() {
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        Member findMember = memberRepository.findReadOnlyById(member1.getId());
        findMember.changeUsername("member2"); //변경 감지 동작하지 않음 (위 메서드의 쿼리힌트-readOnly 옵션 때문)

        em.flush();
    }

    @Test
    public void callCustom() {
        // 사용자 정의 리포지토리를 상속받은 memberRepossitory에서 사용자 정의 메서드를 호출한다.
        List<Member> result = memberRepository.findMeberCustom();
    }

    @Test
    public void jpaEventBaseEntity() throws Exception {
        //given
        Member member = new Member("member1");
        memberRepository.save(member); //@PrePersist
        Thread.sleep(100);
        member.changeUsername("member2");
        em.flush(); //@PreUpdate
        em.clear();
        //when
        Member findMember = memberRepository.findById(member.getId()).get();
        //then
        System.out.println("findMember.getCreatedDate = " + findMember.getCreatedDate());
        System.out.println("findMember.getLastModifiedDate = " + findMember.getLastModifiedDate());
        System.out.println("findMember.getCreatedBy = " + findMember.getCreatedBy());
        System.out.println("findMember.getLastModifiedBy = " + findMember.getLastModifiedBy());
    }

    @Test
    public void projection() {
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        List<UsernameOnly> result = memberRepository.findProjectionsByUsername("m1");

        for(UsernameOnly usernameOnly : result) {
            System.out.println("usernameOnly = " + usernameOnly);
        }

    }

    @Test
    public void dtoProjection() {
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        List<UsernameOnlyDto> result = memberRepository.findProjectionsDtoByUsername("m1");
        List<UsernameOnlyDto> result2 = memberRepository.findDynamicProjectionsDtoByUsername("m1", UsernameOnlyDto.class);

        for(UsernameOnlyDto usernameOnly : result) {
            System.out.println("usernameOnly = " + usernameOnly);
        }

    }
}