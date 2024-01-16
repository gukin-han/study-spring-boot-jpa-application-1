package jpabook.jpashop.domain.Service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.exception.NotEnoughStockException;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.domain.repository.OrderRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static jpabook.jpashop.domain.Order.Status.CANCEL;
import static jpabook.jpashop.domain.Order.Status.ORDER;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception{
        //given
        Member member = createMember();

        final Book book = createBook(10000, "jpa", 10);

        int orderCount = 2;

        //when

        final Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        final Order getOrder = orderRepository.findOne(orderId);

        Assertions.assertThat(ORDER).isEqualTo(getOrder.getStatus());
        Assertions.assertThat(1).isEqualTo(getOrder.getOrderItems().size());
        Assertions.assertThat(10000 * orderCount).isEqualTo(getOrder.getTotalPrice());
        Assertions.assertThat(8).isEqualTo(book.getStockQuantity());

    }

    private Book createBook(int price, String name, int stockQuantity) {
        final Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("member1");
        member.setAddress(new Address("seoul", "seoul street", "123-123"));
        em.persist(member);
        return member;
    }

    @Test
    public void 주문취소() throws Exception{
        //given
        final Member member = createMember();
        final Item book = createBook(10000, "jpa", 10);
        //when
        int orderCount = 2;
        final Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        orderService.cancelOrder(orderId);

        //then
        final Order getOrder = orderRepository.findOne(orderId);

        Assertions.assertThat(CANCEL)
                .isEqualTo(getOrder.getStatus());
        Assertions.assertThat(10)
                .isEqualTo(book.getStockQuantity());
    }

    @Test
    public void 상품주문_재고수량초과() throws Exception{
        //given
        final Member member = createMember();
        final Item item = createBook(10000, "jpa", 10);

        int orderCount = 11;

        //when
        Assertions.assertThatThrownBy(() -> orderService.order(member.getId(), item.getId(), orderCount))
                .isInstanceOf(NotEnoughStockException.class);

        //then
    }

}