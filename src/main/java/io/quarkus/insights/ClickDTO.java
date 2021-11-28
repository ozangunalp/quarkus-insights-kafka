package io.quarkus.insights;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;

@Entity
@Table(name = "clicks")
public class ClickDTO extends PanacheEntityBase {

    @Column(columnDefinition = "serial")
    @Id
    @GeneratedValue
    private Long id;

    public String userId;
    public String xpath;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClickDTO(Click click) {
        this.userId = click.getUserId();
        this.xpath = click.getXpath();
    }

    public ClickDTO() {
    }

}
