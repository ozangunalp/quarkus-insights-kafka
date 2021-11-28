package io.quarkus.insights;

import javax.persistence.Entity;
import javax.persistence.Table;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;

@Entity
@Table(name = "clicks")
public class ClickDTO extends PanacheEntity {

    public String userId;
    public String xpath;

    public ClickDTO(Click click) {
        this.userId = click.getUserId();
        this.xpath = click.getXpath();
    }

    public ClickDTO() {
    }
}
