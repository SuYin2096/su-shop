package com.scarlettech.orderservice.service;

import com.scarlettech.orderservice.dto.OrderLineItemsRequest;
import com.scarlettech.orderservice.dto.OrderRequest;
import com.scarlettech.orderservice.model.Order;
import com.scarlettech.orderservice.model.OrderLineItems;
import com.scarlettech.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest){
       Order order = new Order();
       order.setOrderNumber(UUID.randomUUID().toString());

     List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsRequestList()
               .stream()
               .map(this::mapToDto)
               .toList();

       order.setOrderLineItemsList(orderLineItems);
       orderRepository.save(order);
    }

    private OrderLineItems mapToDto(OrderLineItemsRequest orderLineItemsRequest){
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsRequest.getPrice());
        orderLineItems.setQuantity(orderLineItemsRequest.getQuantity());
        orderLineItems.setSkuCode(orderLineItems.getSkuCode());
        return orderLineItems;
    }
}
