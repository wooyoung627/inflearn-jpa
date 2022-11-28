package 연관관계매핑.ntomchange;

import javax.persistence.*;

// 연결 테이블을 엔티티로 승격
@Entity
public class MemberProduct5 {

    @Id@GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member5 member5;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private Product5 product5;


}
