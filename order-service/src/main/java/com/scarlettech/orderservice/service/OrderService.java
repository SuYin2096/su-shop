package com.scarlettech.orderservice.service;

import com.scarlettech.orderservice.dto.InventoryResponse;
import com.scarlettech.orderservice.dto.OrderLineItemsRequest;
import com.scarlettech.orderservice.dto.OrderRequest;
import com.scarlettech.orderservice.model.Order;
import com.scarlettech.orderservice.model.OrderLineItems;
import com.scarlettech.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest){
       Order order = new Order();
       order.setOrderNumber(UUID.randomUUID().toString());

     List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsRequestList()
               .stream()
               .map(this::mapToDto)
               .toList();

       order.setOrderLineItemsList(orderLineItems);
      List<String> skuCodes =  order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();
        // call inventory service and place order if product is in the stock
        InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
                .uri("http://invertory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

      boolean allProductInStock =  Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);

        if(allProductInStock){
            orderRepository.save(order);
        }else{
            throw new IllegalArgumentException("Product is out of stock, please try again later.");
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
