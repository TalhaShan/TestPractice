//package com.mine.test2.service;
//
//import com.mine.test2.dto.TransactionRequest;
//import com.mine.test2.dto.TransactionResponse;
//import com.mine.test2.entity.Transaction;
//import com.mine.test2.exceptions.TransactionNotFoundException;
//import com.mine.test2.repository.TransactionRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class TransactionServiceImpl implements TransactionService {
//
//   // private final RefundRepository refundRepository;
//    private final TransactionRepository transactionRepository;
//
//    @Override
//    @Transactional
//    public TransactionResponse initiateTransaction(TransactionRequest request) {  //Debit Add
//
//        Optional<Transaction> existingTransaction = transactionRepository.findTransactionByName(request.getName());
//        if(existingTransaction.isPresent()){
//            throw  new RuntimeException("Transaction already exist");
//        }
//        TransactionResponse transactionResponse = new TransactionResponse();
//        Transaction transaction = new Transaction();
//
//        transaction.setName(request.getName());
//        transaction.setInitialAmount(request.getAmount());
//
//        transactionRepository.save(transaction);
//        transactionResponse.setTransactionId(transaction.getId());
//        transactionResponse.setTransactionName(transaction.getName());
//        return transactionResponse;
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public TransactionResponse getTransactionByName(String name) {
//
//        Optional<Transaction> transaction = transactionRepository.findTransactionByName(name);
//        if(transaction.isEmpty()){
//            throw  new TransactionNotFoundException();
//        }
//        TransactionResponse transactionResponse = new TransactionResponse();
//        transactionResponse.setTransactionId(transaction.get().getId());
//        transactionResponse.setTransactionName(transaction.get().getName());
//        transactionResponse.setAmount(transaction.get().getInitialAmount());
//
//        return transactionResponse;
//    }
//}
