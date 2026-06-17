//package inventory;
//
//import com.test.InventoryManagementService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//public class InventoryManagementServiceTest {
//
//    /*
//    addStock("A" , "W1", 5)
//    addStock("A" , "W2", 7)
//    getAvailableQuantity("A" , "W1")  -> 5
//    getAvailableQuantity("A" , "W2")  -> 7
//     */
//    private InventoryManagementService inventoryManagementService;
//
//
//    @BeforeEach
//    void setUp() {
//        inventoryManagementService = new InventoryManagementService();
//    }
//    @Test
//    void testaddStock(){
//
//       inventoryManagementService.addStock("A" , "W1", 5);
//       inventoryManagementService.addStock("A" , "W2", 7);
//       assert inventoryManagementService.getAvailableQuantity("A" , "W1") == 5;
//    }
//
//    @Test
//    void testTotalAvailableQuantityInWarehouse(){
//        inventoryManagementService.addStock("A" , "W1", 5);
//        inventoryManagementService.addStock("A" , "W2", 7);
//        inventoryManagementService.addStock("B", "W1", 7);
//        inventoryManagementService.addStock("A" , "W1", 5);
//        assert inventoryManagementService.getAvailableQuantity("A","W1") == 10;
//    }
//
//    @Test
//    void testTotalAvailableQuantityAllWarehouses(){
//        inventoryManagementService.addStock("A" , "W1", 5);
//        inventoryManagementService.addStock("A" , "W2", 7);
//        inventoryManagementService.addStock("B", "W1", 7);
//        assert inventoryManagementService.getTotalAvailableQuantity("A") == 12;
//    }
//}
