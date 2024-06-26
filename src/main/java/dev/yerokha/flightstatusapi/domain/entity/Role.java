package dev.yerokha.flightstatusapi.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {

    @Id
    @Column(name = "id", nullable = false, updatable = false, insertable = false)
    private Integer id;

    @Column(name = "code", insertable = false, nullable = false, updatable = false, unique = true, length = 256)
    private String code;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getAuthority() {
        return this.code;
    }
}
