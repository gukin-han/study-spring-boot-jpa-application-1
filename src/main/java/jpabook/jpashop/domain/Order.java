package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jpabook.jpashop.domain.Delivery.Status.*;
import static jpabook.jpashop.domain.Order.Status.*;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // EAGER의 경우 Order를 조회할때 member(FK)도 같이 조인해서 가져온다
    @JoinColumn(name = "member_id") // 실제 컬럼 명 == FK 이름
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    // without Cascade
    // persist(orderItemA)
    // persist(orderItemB)
    // persist(orderItemC)

    // with Cascade
    // persist(order)

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private Status status;  // 주문상태 [ORDER, CANCEL]

    enum Status {
        ORDER, CANCEL
    }

    // 연관관계 메서드
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    // 생성 메서드
    public static Order createOrder(Member member,
                                    Delivery delivery,
                                    OrderItem... orderItems) {

        final Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        order.setStatus(ORDER);
        order.setOrderDate(LocalDateTime.now());

        return order;
    }

    // 비즈니스 로직
    // 주문 취소
    public void cancel() {
        if (delivery.getStatus() == COMPLETE) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다");
        }

        this.setStatus(CANCEL);
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    public int getTotalPrice() {
        return orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();
    }
}
