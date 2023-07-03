package study.datajpa.entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<String> {

    @Id
    private String id;

    @CreatedDate
    private LocalDateTime createdDate;

    public Item(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Item setId(String id) {
        this.id = id;
        return this;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public Item setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
        return this;
    }
    /*
    Item 엔티티는 id 생성전략이 @GeneratedValue가 아니라 객체 생성 시점에 생성자를 통해 생성된다.
    따라서 save시 id가 이미 존재하여 JPA가 새로운 객체로 판단하지 않고 persist가 아닌 merge를 수행하게 된다.(select 후 insert 하게 됨 = 비효율)
    * @CreatedDate는 JPA가 persist하기 전에 호출 됨
     */
    @Override
    public boolean isNew() {
        return createdDate == null;
    }
}
