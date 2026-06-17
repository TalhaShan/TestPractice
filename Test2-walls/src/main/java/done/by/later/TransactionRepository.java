package done.by.later;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByName(String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Transaction t WHERE t.name = :name")
    Optional<Transaction> findByNameForUpdate(@Param("name") String name);
}
