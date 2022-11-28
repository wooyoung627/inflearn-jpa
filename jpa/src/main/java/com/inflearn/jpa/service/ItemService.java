package com.inflearn.jpa.service;

import com.inflearn.jpa.domain.item.Item;
import com.inflearn.jpa.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService{

    private final ItemRepository itemRepository;

    // 이렇게 위임만 하는 service는 controller에서 바로 repository에 바로 접근해도 된다.
    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    @Transactional
    public void  updateItem(Long itemId, String name, int price, int stockQuantity){
        Item findItem = itemRepository.findOne(itemId);

        // 아래처럼 set을 남발하지 말고 엔티티에서 바꾸도록 해야 한다.
        // findItem.change(price, name, stockQuantity);

        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);
        // ...

        // 호출할 필요가 없음 => 영속 엔티티이기 때문에 변경감지
        // itemRepository.save(findItem);
    }

    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }

}
