package done.by.later;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class BankingController {

    private final BankingService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionDto createTransaction(@RequestBody @Valid CreateTransactionRequest req) {
        return TransactionDto.from(service.createTransaction(req.getName(), req.getAmount()));
    }

    @GetMapping("/{name}")
    public TransactionDto getTransaction(@PathVariable String name) {
        return TransactionDto.from(service.getTransaction(name));
    }

    @PostMapping("/{name}/refunds")
    @ResponseStatus(HttpStatus.CREATED)
    public RefundDto createRefund(
            @PathVariable String name,
            @RequestBody @Valid CreateRefundRequest req) {
        return RefundDto.from(service.createRefund(name, req.getRefundName(), req.getAmount()));
    }
}
