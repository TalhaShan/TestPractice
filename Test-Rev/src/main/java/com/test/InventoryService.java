package com.test;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryService {

 Map<String,HashMap<String,Integer>> inventories = new ConcurrentHashMap<>();
 Map<String,Reservation> reservationMap = new ConcurrentHashMap<>();
//Mao(sku,(Map(<warehouse,(qty))>)
    public void addStock(String sku, String warehouseId, int quantity) {
            inventories.computeIfAbsent(sku,k->new HashMap<>())
                    .merge(warehouseId,quantity,Integer::sum);
    }

    public int getAvailableQuantity(String sku, String warehouseId) {
        return inventories.getOrDefault(sku,new HashMap<>())
                .getOrDefault(warehouseId,0);
    }

    public int getTotalAvailableQuantity(String sku) {
        int total = 0;
        for(Map.Entry<String,HashMap<String,Integer>> entry:inventories.entrySet()){
            if(entry.getKey().equals(sku)){
                total= entry.getValue().values().stream().reduce(0, Integer::sum);
            }
        }
        return total;
    }


    public int getTotalAvailableQuantity2(String sku) {

        Map<String, Integer> warehouses = inventories.get(sku);

        if (warehouses == null) {
            return 0;
        }

        int total = 0;

        for (int qty : warehouses.values()) {
            total += qty;
        }

        return total;
    }
    enum ReservationStatus {
        CREATED,
        CONFIRMED,
        CANCELLED
    }

    static class Reservation {
        private final String id;
        private final String sku;
        private final String warehouseId;
        private  ReservationStatus reservationStatus;

        private Reservation(String id, String sku, String warehouseId) {
            this.id = id;
            this.sku = sku;
            this.warehouseId = warehouseId;
            this.reservationStatus = ReservationStatus.CREATED;  //status by default created
        }
        public String getId() {
            return id;
        }

        public String getSku() {
            return sku;
        }

        public String getWarehouseId() {
            return warehouseId;
        }

        public ReservationStatus getStatus() {
            return reservationStatus;
        }

        public void setStatus(ReservationStatus status) {
            this.reservationStatus = status;
        }

    }

    public String reserve(String sku, String warehouseId) {

        int availableQuantity = getAvailableQuantity(sku, warehouseId);
        if(availableQuantity == 0){
            throw new IllegalStateException("Not enough stock for sku: " + sku);
        }

        inventories.get(sku).put(warehouseId,availableQuantity-1);
        String reservationId = UUID.randomUUID().toString();
         Reservation reservation =
                new Reservation(reservationId, sku, warehouseId);
        reservationMap.put(reservationId,reservation);

        return reservationId;
    }

    public void confirmReservation(String id) {
        Reservation reservation = reservationMap.get(id);
       reservation.setStatus(ReservationStatus.CONFIRMED);
    }

    public void cancelReservation(String id) {
        Reservation reservation = reservationMap.get(id);
        if(reservation != null){
            reservation.setStatus(ReservationStatus.CANCELLED);
        }

        inventories.get(reservation.getSku())
                .merge(reservation.getWarehouseId(),1,Integer::sum);


        reservation.setStatus(ReservationStatus.CANCELLED);
    }


}


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
