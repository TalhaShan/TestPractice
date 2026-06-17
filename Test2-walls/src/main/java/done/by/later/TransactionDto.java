package done.by.later;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class TransactionDto {

    private Long id;
    private BigDecimal amount;
    private String name;
    private Long version;
    private List<Refund> refunds  =new ArrayList<>();

    static TransactionDto from(Transaction tx) {
        TransactionDto dto = new TransactionDto();
        dto.setVersion(tx.getVersion());
        dto.setRefunds(tx.getRefunds());
        dto.setName(tx.getName());
        dto.setAmount(tx.getAmount());
        dto.setId(tx.getId());
        return dto;
    }
}
