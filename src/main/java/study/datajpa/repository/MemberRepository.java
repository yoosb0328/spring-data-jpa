package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.Entity;
import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

//@Repository JpaRepository를 상속받음 => @Repository 필요없다.
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    /*
    메서드 이름으로 Query 생성 : 간단한 쿼리의 경우에 사용
     */
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    /*
    스프링 데이터 JPA로 Named Query 사용
     */
    @Query(name = "Member.findByUsername")//설정한 이름의 NamedQuery를 찾아서 실행. 주석처리 해놔도 우선순위에 따라 NamedQuery를 먼저 찾긴 함.
    public List<Member> findByUsername(@Param("username") String username);

    /*
    리포지토리에 쿼리 직접 정의하기
    app 로딩 시점에 쿼리를 모두 파싱해보기 때문에 쿼리에 문법 오류 시 발견 가능.
    메서드 이름을 간단하게 만들 수 있으며, 복잡한 정적 쿼리 필요시 사용 (동적 쿼리는 Querydsl 사용해야 함)
     */
    @Query("select m from Member m where m.username = :username and m.age = :age")
    public List<Member> findUser(@Param("username") String username, @Param("age") int age);

    /*
    @Query로 단순한 값 조회하기. JPA 값 타입도 조회 가능
     */
    @Query("select m.username from Member m")
    public List<String> findUsernameList();

    /*
    @Query로 DTO 조회 : JPQL의 new Operation 사용.
     */
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    public List<MemberDto> findMemberDto();

    /*
    컬렉션 파라미터 바인딩 : 쿼리에 in 절 활용
     */
    @Query("select m from Member m where m.username in :names")
    public List<Member> findByNames(@Param("names") Collection <String> names);

    /*
    컬렉션 타입 반환 함수: 결과 없음 -> 빈 컬렉션
    단건 조회 함수 : 결과 없음 -> null 반환
    2건 이상 : javax.persistence.NonUniqueResultException
     */
    List<Member> findListByUsername(String name); //컬렉션
    Member findMemberByUsername(String name); //단건
    Optional<Member> findOptionalByUsername(String name); //단건 Optional

    /*
    페이징
     */
    @Query(value = "select m from Member m left join m.team t",
    countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);
//
//    @Override
//    Page<Member> findAll(Pageable pageable);
    /*
    Slice로 페이징하게 되면 limit + 1 개를 가져옴
    + totalElements와 TotalPages가 없다. (total을 계산하는 count 쿼리를 날리지 않는다.)
     */
//    Slice<Member> findByAge(int age, Pageable pageable);
    /*
    Count 쿼리 분리 (select 쿼리에 join이 발생할 경우 count 쿼리 또한 불필요한 join으로 조회 성능이 떨어지는 경우를 대비)
    => 복잡한 sql에서 사용, 데이터는 left join, 카운트는 left join 안해도 됨
     */
    @Query(value = "select m from Member m",
            countQuery = "select count(m.username) from Member m")
    Page<Member> findMemberAllCountBy(Pageable pageable);

    /*
    벌크성 수정 쿼리
    @Modifying을 필수로 붙여줘야 함.
    *JPA에서 bulk 연산은 주의해야한다.
    @Modifying(clearAutomatically = true) 설정을 통해 영속성 컨텍스트를 벌크 연산 이후 자동으로 초기화 할 수 있다.
     */
    @Modifying
    @Query("update Member m set m.age = m. age +1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    /*
    fetch 조인 : LAZY 로딩으로 인한 1+N 쿼리 해결. 프록시 객체 대신 실제 객체를 가져오게 됨.
     */
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();
    /*
    엔티티 그래프
    JPQL 없이 한번에 실제 객체 가져오기(내부적으로 fetch join이 실행됨) = fetch조인 간단 버전
    */
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    /*
    쿼리 힌트 -> readOnly : 변경감지를 위한 원본(변경전) 스냅샷을 생성하지 않는다.
     */
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyById(Long id);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

    /*
    Projection : 엔티티 전체가 아닌 일부 필드만 dto를 통해 조회하고자 할 때.
    스프링데이터JPA가 UsernameOnly 인터페이스의 프록시 객체를 주입해줌..
    Projection 한계 :  대상이 Root 엔티티를 벗어나 조인이 발생하면  root가 아닌 엔티티에 대하여 select절 최적화가 되지 않음.

     */
    List<UsernameOnly> findProjectionsByUsername(@Param("username") String username);
    /*
    Projection
    interface가 아닌 구체적 dto class를 이용하므로 프록시 객체 X
     */
    List<UsernameOnlyDto> findProjectionsDtoByUsername(@Param("username") String username);

    /*
    동적 Projection
     */
    <T> List<T> findDynamicProjectionsDtoByUsername(@Param("username") String username, Class<T> type);

}
