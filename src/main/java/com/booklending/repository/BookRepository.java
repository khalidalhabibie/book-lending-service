package com.booklending.repository;

import com.booklending.entity.BookEntity;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<BookEntity, Long> {

  boolean existsByIsbn(String isbn);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT b FROM BookEntity b WHERE b.id = :id")
  Optional<BookEntity> findByIdForUpdate(@Param("id") Long id);
}
