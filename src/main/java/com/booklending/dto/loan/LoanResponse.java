package com.booklending.dto.loan;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoanResponse {

  private Long id;
  private Long bookId;
  private Long memberId;
  private Instant borrowedAt;
  private Instant dueDate;
  private Instant returnedAt;
}
