package done.by.later;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface RefundRepository extends JpaRepository<Refund, Long> {

    boolean existsByName(String name);

    Optional<Refund> findByName(String name);

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM Refund r WHERE r.transaction = :tx")
    BigDecimal sumRefundedAmount(@Param("tx") Transaction tx);
}
