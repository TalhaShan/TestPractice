//package com.mine.test2.service;
//
//import com.mine.test2.dto.RefundRequest;
//import com.mine.test2.entity.Transaction;
//import com.mine.test2.exceptions.TransactionNotFoundException;
//import com.mine.test2.repository.RefundRepository;
//import com.mine.test2.repository.TransactionRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class RefundServiceImpl implements RefundService{
//
//    private final RefundRepository refundRepository;
//    private final TransactionRepository transactionRepository;
//
//    @Override
//    @Transactional
//    public boolean refundRequest(RefundRequest refundRequest) {
//
//        Optional<Transaction> transaction = transactionRepository.findById(refundRequest.getTransactionId());
//        if(transaction.isEmpty()){
//            throw new TransactionNotFoundException();
//        }
//        BigDecimal amount = transaction.get().getRemainingAmount().subtract(refundRequest.getRefundAmount());
//        if(BigDecimal.ZERO.compareTo(amount) <= 0){
//            return false;
//        }
//
//        transaction.get().setRemainingAmount(amount);
//        transactionRepository.save(transaction.get());
//        return true;
//    }
//}
