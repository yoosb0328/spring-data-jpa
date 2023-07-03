package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.paging.PageCustom;
import study.datajpa.paging.PageableCustom;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }
    /*
    도메인 클래스 컨버터 동작 메서드 : 트랜잭션이 없는 범위에서 repository가 엔티티를 조회하는 것이므로 단순 조회를 위해서만 사용할 것.
     */
    @GetMapping("/members2/{id}")
    public String findMember(@PathVariable("id") Member member) {
        return member.getUsername();
    }
    /*
    Spring Data의 페이징 기능
    Pageable 객체를 매개변수로 받으면 PageRequest 객체를 생성해 주입해줌
    ex: https://localhost:8080/members?page=0&size=3&sort=id,desc&sort=username
    ->매개변수로 파라미터들을 받아 PageRequest 객체 생성하여 주입.
    size default = 20 => application.yml에서 글로벌 설정 가능.
    @PageableDefault를 통해 개별 설정 가능
     */
    @GetMapping("/members")
    private Page<MemberDto> list(@PageableDefault(size = 5) Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        //엔티티를 api를 통해 외부에 노출하는 것은 프로그램 설계를 외부에 노출하는 것이다. dto에 담아서 return할 것.
        Page<MemberDto> map = page.map(member -> new MemberDto(member));
        return map;
    }
    /*
    Pageable과 Page 클래스를 커스텀하여 PageNumber가 1부터 시작하도록 설정
     */
    @GetMapping("/members2")
    private PageCustom<MemberDto> customList(@PageableDefault(size = 5) Pageable pageable) {
        Page<MemberDto> map = memberRepository.findAll(pageable).map(member -> new MemberDto(member));
        return new PageCustom<MemberDto>(map);
    }
//
//    @PostConstruct
//    public void init() {
//        for(int i = 0; i < 100; i++) {
//            memberRepository.save(new Member("user" + i, i));
//        }
//    }
}
