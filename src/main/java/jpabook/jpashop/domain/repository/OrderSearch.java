package jpabook.jpashop.domain.repository;

import jpabook.jpashop.domain.Order;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderSearch {

    private String memberName;
    private Order.Status status;
}
