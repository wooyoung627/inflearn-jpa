package 연관관계매핑.ntomchange;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Member5 {

    @Id
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    @OneToMany(mappedBy = "member5")
    private List<MemberProduct5> memberProduct5s = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
