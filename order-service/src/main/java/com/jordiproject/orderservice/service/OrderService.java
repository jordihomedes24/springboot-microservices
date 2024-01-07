package com.jordiproject.orderservice.service;

import com.jordiproject.orderservice.dto.OrderLineItemDto;
import com.jordiproject.orderservice.dto.OrderRequest;
import com.jordiproject.orderservice.model.Order;
import com.jordiproject.orderservice.model.OrderLineItem;
import com.jordiproject.orderservice.repository.OrderRepository;
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
    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItem> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        //We call inventory Service to see if products are on stock (we use Synchronous communication)
        Boolean result = webClient.get()
                        .uri("http://localhost:8082/api/inventory")
                        .retrieve()
                        .bodyToMono(Boolean.class)
                        .block();

        if (Boolean.TRUE.equals(result)) {
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Product is not in stock");
        }

    }

    private OrderLineItem mapDto(OrderLineItemDto orderLineItemDto) {
        return OrderLineItem.builder()
                .price(orderLineItemDto.getPrice())
                .quantity(orderLineItemDto.getQuantity())
                .skuCode(orderLineItemDto.getSkuCode())
                .build();
    }
}
