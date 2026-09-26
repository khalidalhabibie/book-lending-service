package com.booklending.repository;

import com.booklending.entity.LoanEntity;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<LoanEntity, Long> {

  long countByMemberIdAndReturnedAtIsNull(Long memberId);

  boolean existsByMemberIdAndReturnedAtIsNullAndDueDateBefore(Long memberId, Instant now);
}
