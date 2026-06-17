//package com.mine.test2.controllers;
//
//import com.mine.test2.dto.TransactionRequest;
//import com.mine.test2.dto.TransactionResponse;
//import com.mine.test2.service.TransactionService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("api/transaction/v1")
//@RequiredArgsConstructor
//public class TransactionController {
//
//    private final TransactionService transactionService;
//
//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public TransactionResponse createTransaction(@Valid @RequestBody TransactionRequest transaction) {
//        return transactionService.initiateTransaction(transaction);
//    }
//
//    @GetMapping
//    @ResponseStatus(HttpStatus.OK)
//    public TransactionResponse getTransactionByName(@RequestParam("name") String name) {
//        return transactionService.getTransactionByName(name);
//    }
//}
