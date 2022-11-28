package com.inflearn.jpa.service;

import com.inflearn.jpa.domain.item.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;

@SpringBootTest
class ItemServiceTest {

    @Autowired
    EntityManager em;

    @Test
    void updateTest() throws Exception {
        Book book = em.find(Book.class, 1L);

        // TX
        book.setName("test123");

        // 변경감지 == dirty checking
        // TX commit
    }
}