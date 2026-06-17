package done.by.later;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RefundDto {
    protected long id;
    private String name;
    private BigDecimal amount;

    static RefundDto from(Refund refund) {
        RefundDto dto = new RefundDto();
        refund.setId(refund.getId());
        refund.setName(refund.getName());
        refund.setAmount(refund.getAmount());
        return dto;
    }
}
