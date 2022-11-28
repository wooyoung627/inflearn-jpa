package com.inflearn.jpa.controller;

import com.inflearn.jpa.domain.Member;
import com.inflearn.jpa.repository.OrderSearch;
import com.inflearn.jpa.service.OrderService;
import com.inflearn.jpa.domain.Order;
import com.inflearn.jpa.domain.item.Item;
import com.inflearn.jpa.service.ItemService;
import com.inflearn.jpa.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/order")
    public String createForm(Model model) {
        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";
    }

    @PostMapping("/order")
    public String order(@RequestParam("memberId") Long memberId
            , @RequestParam("itemId") Long itemId
            , @RequestParam("count") int count) {

        // controller에서 find 할 수도 있지만 메서드로 빼놓으면 동작이 단순해짐
        orderService.order(memberId, itemId, count);
        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model){
        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders", orders);
        return "order/orderList";
    }

    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId){
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }

}
