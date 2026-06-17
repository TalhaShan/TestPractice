//package com.mine.test2.controllers;
//
//import com.mine.test2.dto.RefundRequest;
//import com.mine.test2.service.RefundService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("api/refund/v1")
//@RequiredArgsConstructor
//public class RefundController {
//
//    private final RefundService refundService;
//
//    @PostMapping
//    @ResponseStatus(HttpStatus.OK)
//    public boolean refund(@RequestBody RefundRequest refundRequest) {
//        return refundService.refundRequest(refundRequest);
//    }
//}
