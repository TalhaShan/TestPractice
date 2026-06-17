package com.test;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        InventoryService service = new InventoryService();

        service.addStock("A", "W1", 2);

        String r1 = service.reserve("A", "W1");

        System.out.println(
                service.getAvailableQuantity("A", "W1")
        ); // 1

        service.confirmReservation(r1);

        String r2 = service.reserve("A", "W1");

        System.out.println(
                service.getAvailableQuantity("A", "W1")
        ); // 0

        service.cancelReservation(r2);

        System.out.println(
                service.getAvailableQuantity("A", "W1")
        ); // 1
    }
}
