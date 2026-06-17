//package com.test;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class InventoryManagementService {
//
//   // ConcurrentHashMap<String, Integer> inventory = new ConcurrentHashMap<>();
//    private ConcurrentHashMap<String, HashMap<String,Integer>> inventory = new ConcurrentHashMap<>();
//    private ConcurrentHashMap<String, String> reservationStatus = new ConcurrentHashMap<>();
//
//
//    public void addStock(String sku, String warehouseId, int quantity) {
//       inventory.computeIfAbsent(sku, k -> new HashMap<>()).merge(warehouseId, quantity, Integer::sum);
//
//    }
//
//    public int getAvailableQuantity(String sku, String warehouseId) {
//        return inventory.getOrDefault(sku, new HashMap<>()).getOrDefault(warehouseId, 0);
//    }
//
//    public int getTotalAvailableQuantity(String sku) {
//        return inventory.getOrDefault(sku, new HashMap<>()).values().stream().reduce(0, Integer::sum);
//    }
//
//    public String reserve(String sku, String warehouseId) {
//        reservationStatus.put("1",warehouseId);
//        inventory.getOrDefault(sku, new HashMap<>()).merge(warehouseId, 1, Integer::sum);
//        return "1";
//
//    }
//    void confirmReservation(String id) {
//
//    }
//    void cancelReservation(String id) {
//
//    }
//
//    @Transactional
//    public Integer reserve(String sku) {
//        var amount = db.fetch("select amount from stocks where sku = ? limit 1 FOR UPDATE", sku);
//
//        //OR
//        //
//        var amount = db.fetch("select version, amount from stocks where sku = ? limit 1 ", sku);
//        if(amount>=1) {
//            db.execute("update stocks set amount = ? where sku = ?", sku, amount - 1);
//            db.insert("insert into reservations ...");
//        }
//        return amount;
//    }
//}
//
//
//DEAUT ISOLATION LEVEL POTGRE
///*
//addStock("A" , "W1", 5)
//addStock("A" , "W1", 7)
//getAvailableQuantity("A" , "W1") -> 12
//
//
//addStock("A" , "W1", 5)
//addStock("A" , "W2", 7)
//getAvailableQuantity("A" , "W1")  -> 5
//getAvailableQuantity("A" , "W2")  -> 7
//
//
//addStock("A" , "W1", 5)
//addStock("A" , "W2", 7)
//addStock("A" , "W1", 3)
//getAvailableQuantity("A" , "W1")  -> 8
//getAvailableQuantity("A" , "W2")  -> 7
//
//
//addStock("A" , "W1", 5)
//addStock("A" , "W2", 7)
//addStock("A" , "W1", 3)
//addStock("B" , "W1", 2)
//getAvailableQuantity("A" , "W1")  -> 8
//getAvailableQuantity("A" , "W2")  -> 7
//getAvailableQuantity("B" , "W1")  -> 2
//
//
// */
