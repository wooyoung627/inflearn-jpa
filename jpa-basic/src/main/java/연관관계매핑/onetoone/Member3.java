package 연관관계매핑.onetoone;

import javax.persistence.*;

// 주테이블(Member3)에 외래키(LOCKER_ID)
@Entity
public class Member3 {

    @Id
    // GenerationType.IDENTITY = persist시 바로 insert query 날라감 (바로 영속상태로)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    // 연관관계의 주인
    @OneToOne
    @JoinColumn(name = "LOCKER_ID")
    private Locker locker;
}
