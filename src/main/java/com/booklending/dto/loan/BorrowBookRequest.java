package com.booklending.dto.loan;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BorrowBookRequest {

  @NotNull private Long memberId;

  @NotNull private Long bookId;
}
