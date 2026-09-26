package com.booklending.controller;

import com.booklending.common.response.ApiResponse;
import com.booklending.dto.loan.BorrowBookRequest;
import com.booklending.dto.loan.LoanResponse;
import com.booklending.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

  private final LoanService loanService;

  @PostMapping
  public ResponseEntity<ApiResponse<LoanResponse>> borrow(
      @Valid @RequestBody BorrowBookRequest request) {

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(loanService.borrow(request)));
  }

  @PostMapping("/{id}/return")
  public ResponseEntity<ApiResponse<LoanResponse>> returnBook(@PathVariable Long id) {

    return ResponseEntity.ok(ApiResponse.success(loanService.returnBook(id)));
  }
}
