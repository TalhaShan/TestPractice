//package com.mine.test2.entity;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.Table;
//import jakarta.validation.constraints.NotNull;
//import lombok.AccessLevel;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@Table(name = "refund")
//@Entity
//@NoArgsConstructor(access = AccessLevel.PUBLIC)
//@AllArgsConstructor(access = AccessLevel.PROTECTED)
//@Getter
//@Setter
//public class Refund {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "transaction_id")
//    private Transaction transaction;
//
//    @NotNull
//    private double refundAmount;
//
//    private String name;
//
//}
