package 연관관계매핑.ntomchange;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Product5 {

    @Id
    @GeneratedValue
    @Column(name = "PRODUCT_ID")
    private Long id;

    @Column(name = "PRODUCT_NAME")
    private String name;

    @OneToMany(mappedBy = "product5")
    private List<MemberProduct5> memberProduct5s = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
