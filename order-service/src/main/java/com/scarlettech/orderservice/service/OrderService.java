package com.scarlettech.orderservice.service;

import com.scarlettech.orderservice.dto.OrderLineItemsRequest;
import com.scarlettech.orderservice.dto.OrderRequest;
import com.scarlettech.orderservice.model.Order;
import com.scarlettech.orderservice.model.OrderLineItems;
import com.scarlettech.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;

    public void placeOrder(OrderRequest orderRequest){
       Order order = new Order();
       order.setOrderNumber(UUID.randomUUID().toString());

     List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsRequestList()
               .stream()
               .map(this::mapToDto)
               .toList();

       order.setOrderLineItemsList(orderLineItems);

        // call inventory service and place order if product is in the stock
        Boolean result = webClient.get()
                .uri("http://localhost:8083/api/inventory")
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if(result){
            orderRepository.save(order);
        }else{
            throw new IllegalArgumentException("Product is not in stock, please try again later.");
        }

    }

    private OrderLineItems mapToDto(OrderLineItemsRequest orderLineItemsRequest){
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsRequest.getPrice());
        orderLineItems.setQuantity(orderLineItemsRequest.getQuantity());
        orderLineItems.setSkuCode(orderLineItems.getSkuCode());
        return orderLineItems;
    }
}
