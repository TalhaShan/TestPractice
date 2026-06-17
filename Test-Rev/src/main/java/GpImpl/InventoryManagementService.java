package GpImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class InventoryManagementService {

        // sku -> warehouse -> quantity
        private final Map<String, Map<String, Integer>> inventory = new HashMap<>();

        // reservationId -> reservation
        private final Map<String, Reservation> reservations = new HashMap<>();

        enum ReservationStatus {
            CREATED,
            CONFIRMED,
            CANCELLED
        }

        static class Reservation {

            private final String id;
            private final String sku;
            private final String warehouseId;

            private ReservationStatus status;

            public Reservation(String id, String sku, String warehouseId) {
                this.id = id;
                this.sku = sku;
                this.warehouseId = warehouseId;
                this.status = ReservationStatus.CREATED;
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
                return status;
            }

            public void setStatus(ReservationStatus status) {
                this.status = status;
            }
        }

        // =========================
        // STOCK
        // =========================

        public void addStock(String sku, String warehouseId, int quantity) {

            validateSku(sku);
            validateWarehouse(warehouseId);

            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }

            inventory
                    .computeIfAbsent(sku, k -> new HashMap<>())
                    .merge(warehouseId, quantity, Integer::sum);
        }

        public int getAvailableQuantity(String sku, String warehouseId) {

            return inventory
                    .getOrDefault(sku, Map.of())
                    .getOrDefault(warehouseId, 0);
        }

        public int getTotalAvailableQuantity(String sku) {

            return inventory
                    .getOrDefault(sku, Map.of())
                    .values()
                    .stream()
                    .mapToInt(Integer::intValue)
                    .sum();
        }

        // =========================
        // RESERVATION
        // =========================

        public String reserve(String sku, String warehouseId) {

            validateSku(sku);
            validateWarehouse(warehouseId);

            int available = getAvailableQuantity(sku, warehouseId);

            if (available <= 0) {
                throw new IllegalStateException(
                        "No stock available for sku=" + sku +
                                " warehouse=" + warehouseId
                );
            }

            // decrement stock immediately
            inventory.get(sku)
                    .put(warehouseId, available - 1);

            String reservationId = UUID.randomUUID().toString();

            Reservation reservation =
                    new Reservation(reservationId, sku, warehouseId);

            reservations.put(reservationId, reservation);

            return reservationId;
        }

        public void confirmReservation(String id) {

            Reservation reservation = getReservationOrThrow(id);

            if (reservation.getStatus() == ReservationStatus.CANCELLED) {
                throw new IllegalStateException(
                        "Cannot confirm cancelled reservation"
                );
            }

            if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
                throw new IllegalStateException(
                        "Reservation already confirmed"
                );
            }

            reservation.setStatus(ReservationStatus.CONFIRMED);
        }

        public void cancelReservation(String id) {

            Reservation reservation = getReservationOrThrow(id);

            if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
                throw new IllegalStateException(
                        "Cannot cancel confirmed reservation"
                );
            }

            if (reservation.getStatus() == ReservationStatus.CANCELLED) {
                throw new IllegalStateException(
                        "Reservation already cancelled"
                );
            }

            // restore stock
            inventory
                    .get(reservation.getSku())
                    .merge(reservation.getWarehouseId(), 1, Integer::sum);

            reservation.setStatus(ReservationStatus.CANCELLED);
        }

        // =========================
        // HELPERS
        // =========================

        private Reservation getReservationOrThrow(String id) {

            Reservation reservation = reservations.get(id);

            if (reservation == null) {
                throw new IllegalArgumentException(
                        "Reservation not found: " + id
                );
            }

            return reservation;
        }

        private void validateSku(String sku) {

            if (sku == null || sku.isBlank()) {
                throw new IllegalArgumentException("Invalid sku");
            }
        }

        private void validateWarehouse(String warehouseId) {

            if (warehouseId == null || warehouseId.isBlank()) {
                throw new IllegalArgumentException("Invalid warehouse");
            }
        }

        // =========================
        // DEMO
        // =========================

        public static void main(String[] args) {

            InventoryManagementService service = new InventoryManagementService();

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

