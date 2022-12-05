package study.datajpa.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<String> {
    @Id
//    @GeneratedValue
    private String id;

    @CreatedDate
    private LocalDateTime createdDate;

    public Item(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    /**
     * @GenerateValue를 하지 않고 Id를 String으로 썼을때
     * new Item("A") 의 경우 JpaRepository.save(Entity entity) isNew에서 false 떨어져(이미 id가 있음) merge로 들어감
     * Persistable을 구현해서 isNew를 직접 구현해줘야함
     */
    @Override
    public boolean isNew() {
        return createdDate == null;
    }
}
