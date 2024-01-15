package jpabook.jpashop.domain.Service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    EntityManager em;

    @Test
    @Rollback(false) // insert를 확인하고 싶은 경우
    public void 회원가입() throws Exception{
        //given
        final Member member = new Member();
        member.setName("kim");

        //when
        final Long savedId = memberService.join(member);

        //then
        em.flush(); // rollback false 하거나, flush()를 하는 방식으로
        assertEquals(member, memberRepository.findOne(savedId));
    }

    @Test
    public void 중복_회원_예외() throws Exception{
        //given
        final Member member1 = new Member();
        member1.setName("kim1");
        final Member member2 = new Member();
        member2.setName("kim1");

        //when

        memberService.join(member1);
        assertThrows(IllegalStateException.class,
                () -> memberService.join(member2));

    }
}