package com.inflearn.jpa.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("M") // single table 구분
@Getter
@Setter
public class Movie extends Item {

    private String director;
    private String actor;

}
