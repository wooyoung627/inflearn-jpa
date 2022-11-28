package com.inflearn.jpa.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("B") // single table 구분
@Getter
@Setter
public class Book extends Item{

    private String author;
    private String isbn;

}
