package GpImplTest;

import GpImpl.InventoryManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryServiceTest {

    private InventoryManagementService service;

    @BeforeEach
    void setup() {
        service = new InventoryManagementService();
    }

    // =========================
    // ADD STOCK
    // =========================

    @Test
    void shouldAddStockToNewWarehouse() {

        service.addStock("A", "W1", 5);

        assertEquals(
                5,
                service.getAvailableQuantity("A", "W1")
        );
    }

    @Test
    void shouldAccumulateStockForSameWarehouse() {

        service.addStock("A", "W1", 5);
        service.addStock("A", "W1", 7);

        assertEquals(
                12,
                service.getAvailableQuantity("A", "W1")
        );
    }

    @Test
    void shouldKeepSeparateWarehousesIndependent() {

        service.addStock("A", "W1", 5);
        service.addStock("A", "W2", 7);

        assertEquals(
                5,
                service.getAvailableQuantity("A", "W1")
        );

        assertEquals(
                7,
                service.getAvailableQuantity("A", "W2")
        );
    }

    @Test
    void shouldReturnTotalAvailableQuantity() {

        service.addStock("A", "W1", 5);
        service.addStock("A", "W2", 7);

        assertEquals(
                12,
                service.getTotalAvailableQuantity("A")
        );
    }

    @Test
    void shouldReturnZeroForUnknownSku() {

        assertEquals(
                0,
                service.getAvailableQuantity("UNKNOWN", "W1")
        );
    }

    @Test
    void shouldThrowForNegativeQuantity() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addStock("A", "W1", -1)
        );
    }

    // =========================
    // RESERVE
    // =========================

    @Test
    void shouldReserveStockSuccessfully() {

        service.addStock("A", "W1", 2);

        String reservationId =
                service.reserve("A", "W1");

        assertNotNull(reservationId);

        assertEquals(
                1,
                service.getAvailableQuantity("A", "W1")
        );
    }

    @Test
    void shouldFailReservationWhenNoStockAvailable() {

        service.addStock("A", "W1", 1);

        service.reserve("A", "W1");

        assertThrows(
                IllegalStateException.class,
                () -> service.reserve("A", "W1")
        );
    }

    // =========================
    // CONFIRM
    // =========================

    @Test
    void shouldConfirmReservation() {

        service.addStock("A", "W1", 1);

        String reservationId =
                service.reserve("A", "W1");

        service.confirmReservation(reservationId);

        assertEquals(
                0,
                service.getAvailableQuantity("A", "W1")
        );
    }

    @Test
    void shouldFailConfirmForUnknownReservation() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.confirmReservation("invalid-id")
        );
    }

    @Test
    void shouldFailConfirmAlreadyConfirmedReservation() {

        service.addStock("A", "W1", 1);

        String reservationId =
                service.reserve("A", "W1");

        service.confirmReservation(reservationId);

        assertThrows(
                IllegalStateException.class,
                () -> service.confirmReservation(reservationId)
        );
    }

    // =========================
    // CANCEL
    // =========================

    @Test
    void shouldCancelReservationAndRestoreStock() {

        service.addStock("A", "W1", 1);

        String reservationId =
                service.reserve("A", "W1");

        service.cancelReservation(reservationId);

        assertEquals(
                1,
                service.getAvailableQuantity("A", "W1")
        );
    }

    @Test
    void shouldFailCancelConfirmedReservation() {

        service.addStock("A", "W1", 1);

        String reservationId =
                service.reserve("A", "W1");

        service.confirmReservation(reservationId);

        assertThrows(
                IllegalStateException.class,
                () -> service.cancelReservation(reservationId)
        );
    }

    @Test
    void shouldFailCancelAlreadyCancelledReservation() {

        service.addStock("A", "W1", 1);

        String reservationId =
                service.reserve("A", "W1");

        service.cancelReservation(reservationId);

        assertThrows(
                IllegalStateException.class,
                () -> service.cancelReservation(reservationId)
        );
    }

    // =========================
    // VALIDATION
    // =========================

    @Test
    void shouldRejectBlankSku() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addStock("", "W1", 1)
        );
    }

    @Test
    void shouldRejectBlankWarehouse() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addStock("A", "", 1)
        );
    }
}
