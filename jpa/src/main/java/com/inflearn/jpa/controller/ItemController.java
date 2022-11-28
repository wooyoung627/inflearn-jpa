package com.inflearn.jpa.controller;

import com.inflearn.jpa.domain.item.Book;
import com.inflearn.jpa.domain.item.Item;
import com.inflearn.jpa.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model){
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form){
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/";
    }

    @GetMapping("/items")
    public String list(Model model){
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    @GetMapping("/items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model){
        Book item = (Book) itemService.findOne(itemId);

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    /**
     * 준영속 엔티티를 수정하는 2가지 방법
     * 1. 변경 감지 기능 사용
     * 영속성 컨텍스트에서 엔티티를 다시 조회한 후에 데이터를 수정하는 방법
     * 트랜잭션 안에서 엔티티를 다시 조회, 변경할 값 선택 => 트랜잭션 커밋 시점에 변경 감지(Dirty Checking)가 동작해서 데이터베이스에 UPDATE SQL 실행
     *
     * 2. 병합(merge) 사용
     * 준영속 상태의 엔티티를 영속상태로 변경할 때 사용
     * 식별자로 아이템을 찾아서(영속 엔티티) 넘긴 아이템의 모든 값을 다 밀어넣음(변경 감지)
     *
     * % 변경 감지 기능을 사용한다면 원하는 속성들만 업데이트 가능하지만 병합을 사용하면 모든 값을 바꾼다.(null 위험) %
     * !! 엔티티를 변경할 땐 항상 변경 감지를 사용해야함 !!
     */
    @PostMapping("/items/{itemId}/edit")
    public String updateItem(@PathVariable Long itemId, @ModelAttribute("form") BookForm form){

        // ID가 있음 => DB에 갔다온 데이터를 준영속 상태의 객체라고 함
        // JPA가 식별할 수 있는 ID(식별자)를 가지고 있음
        // 영속성 컨텍스트가 더는 관리하지 않음
//        Book book = new Book();
//        book.setId(form.getId());
//        book.setName(form.getName());
//        book.setPrice(form.getPrice());
//        book.setStockQuantity(form.getStockQuantity());
//        book.setAuthor(form.getAuthor());
//        book.setIsbn(form.getIsbn());
//        itemService.saveItem(book);

        // 위처럼 엔티티를 생성하지 말고 아래와 같이 서비스에서 파라미터들을 보내 업데이트하는 것이 좋다.
        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());
        return "redirect:/items";
    }

}
