package inventory;

import com.test.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InventoryTest {

    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
          inventoryService = new InventoryService();
    }
    @Test
    public void addStock() {
        inventoryService.addStock("A","W1", 10);
        inventoryService.addStock("B","W1", 10);
        inventoryService.addStock("B","W1", 10);

        assertEquals(10,inventoryService.getAvailableQuantity("A", "W1"), 10);
        assertEquals(20,inventoryService.getTotalAvailableQuantity("B"));
    }
}
