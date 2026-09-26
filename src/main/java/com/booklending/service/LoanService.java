package com.booklending.service;

import com.booklending.dto.loan.BorrowBookRequest;
import com.booklending.dto.loan.LoanResponse;

public interface LoanService {

  LoanResponse borrow(BorrowBookRequest request);

  LoanResponse returnBook(Long loanId);
}
