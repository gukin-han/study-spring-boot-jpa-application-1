package jpabook.jpashop.domain.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em; // 스프링 부트 사용시 PersistenceContext 어노테이션 대신 생성자 주입 사용 가능

    public void save(Member member) {
        em.persist(member); // 영속성 컨텍스트에 올라가면 id값이 주어진다 (PK) 따라서 DB 삽입 전에 id 필드가 채워진다
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList(); // entity 오브젝트에 대한 조회를 한다 (차이점)
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
