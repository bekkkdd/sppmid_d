package kz.bitlab.robygroup.sppmid.core.models.currency;

import kz.bitlab.robygroup.sppmid.core.models.entities.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
        name = "seq",
        sequenceName = "s_currencies",
        initialValue = 1,
        allocationSize = 1
)
public class Currencies extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "tenge_buy_ratio")
    private double tengeBuyRatio;

    @Column(name = "tenge_sell_ratio")
    private double tengeSellRatio;

}
