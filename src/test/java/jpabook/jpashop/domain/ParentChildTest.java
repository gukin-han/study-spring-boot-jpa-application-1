package jpabook.jpashop.domain;

import jpabook.jpashop.domain.test.Child;
import jpabook.jpashop.domain.test.Parent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@SpringBootTest
@Transactional
public class ParentChildTest {

    @Autowired
    EntityManager em;

    @Test
    @Rollback(value = false)
    void simpleTest() {

        final Parent parent = new Parent();
        parent.setName("parent");
        final Child child = new Child();
        child.setName("child");

        parent.getChildren().add(child);
        child.setParent(parent);

        em.persist(parent);
        em.persist(child);

        em.flush();
        em.clear();

        final Parent parent1 = em.find(Parent.class, parent.getId());
        em.remove(parent1);

    }
}
