package 연관관계매핑;

import javax.persistence.*;

@Entity
@Table
public class Member2 {

    @Id
    // GenerationType.IDENTITY = persist시 바로 insert query 날라감 (바로 영속상태로)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    // 연관관계의 주인
    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team2 team;

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

    public Team2 getTeam() {
        return team;
    }

    // setter말고 다른 메서드 명을 사용하자
    public void changeTeam(Team2 team) {
        this.team = team;

        // 양방향 연관관계
        team.getMembers().add(this);
    }
}
