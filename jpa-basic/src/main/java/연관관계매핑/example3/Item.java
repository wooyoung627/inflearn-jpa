package 연관관계매핑.example3;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Item {

    @Id @GeneratedValue
    @Column(name = "ITEM_ID")
    private Long id;

    private String name;

    private int price;

    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
//    @JoinTable(name = "ITEM_CATEGORY")
    private List<Category> categories = new ArrayList<>();

}
