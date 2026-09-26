package com.booklending.repository;

import com.booklending.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<BookEntity, Long> {

  boolean existsByIsbn(String isbn);
}
