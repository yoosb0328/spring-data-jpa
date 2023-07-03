package study.datajpa.dto;

import lombok.Data;
import org.springframework.context.event.EventListener;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import study.datajpa.entity.BaseEntity;
import study.datajpa.entity.Member;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import java.time.LocalDateTime;


@Data
public class MemberDto extends BaseEntity {
    private Long id;
    private String username;
    private String teamName;
    public MemberDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }

    public MemberDto(Member member) {
        super.setCreatedBy(member.getCreatedBy());
        super.setCreatedDate(member.getCreatedDate());
        super.setLastModifiedBy(member.getLastModifiedBy());
        super.setLastModifiedDate(member.getLastModifiedDate());

        this.id = member.getId();
        this.username = member.getUsername();
        if(member.getTeam() != null) {
            this.teamName = member.getTeam().getName();
        }
    }
}
