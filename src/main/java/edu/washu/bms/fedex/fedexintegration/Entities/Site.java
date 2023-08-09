package edu.washu.bms.fedex.fedexintegration.Entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CATISSUE_SITE")
@Getter
@Setter
@Data
public class Site {
    @Id
    private Long identifier;
    @Column(name = "Name")
    private String Name;
}
