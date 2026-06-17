package done.by.later;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateTransactionRequest {
    private String name;
    private BigDecimal amount;
}
