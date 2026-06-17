package done.by.later;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "refunds")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected long id;

    private String name;

    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY , optional = false) //??
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;
}
