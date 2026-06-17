package done.by.later;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateRefundRequest {

    private String refundName;
    private BigDecimal amount;
}
