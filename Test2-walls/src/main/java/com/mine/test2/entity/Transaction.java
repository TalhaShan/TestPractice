//package com.mine.test2.entity;
//
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.OneToMany;
//import jakarta.persistence.Table;
//import jakarta.persistence.Version;
//import jakarta.validation.constraints.NotNull;
//import lombok.AccessLevel;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.math.BigDecimal;
//import java.util.Set;
//
//@Entity
//@Table(name = "transactions")
//@NoArgsConstructor(access = AccessLevel.PUBLIC)
//@AllArgsConstructor(access = AccessLevel.PROTECTED)
//@Getter
//@Setter
//public class Transaction {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Version
//    private Integer version;
//
//    @NotNull
//    private BigDecimal initialAmount;
//
//    private BigDecimal remainingAmount;
//
//    @NotNull
//    private String name;
//
//    @OneToMany(mappedBy = "transaction")
//    private Set<Refund> refunds ;
//
//}
