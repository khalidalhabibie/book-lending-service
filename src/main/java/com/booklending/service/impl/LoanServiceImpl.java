package com.booklending.service.impl;

import com.booklending.common.exception.BusinessException;
import com.booklending.common.exception.ErrorCode;
import com.booklending.common.exception.ErrorMessage;
import com.booklending.dto.loan.BorrowBookRequest;
import com.booklending.dto.loan.LoanResponse;
import com.booklending.entity.BookEntity;
import com.booklending.entity.LoanEntity;
import com.booklending.entity.MemberEntity;
import com.booklending.repository.BookRepository;
import com.booklending.repository.LoanRepository;
import com.booklending.repository.MemberRepository;
import com.booklending.service.LoanService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

  private final LoanRepository loanRepository;
  private final BookRepository bookRepository;
  private final MemberRepository memberRepository;

  @Value("${library.max-active-loans}")
  private int maxActiveLoans;

  @Value("${library.loan-duration-days}")
  private int loanDurationDays;

  @Override
  @Transactional
  public LoanResponse borrow(BorrowBookRequest request) {
    MemberEntity member =
        memberRepository
            .findById(request.getMemberId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        ErrorCode.MEMBER_NOT_FOUND,
                        ErrorMessage.MEMBER_NOT_FOUND,
                        HttpStatus.NOT_FOUND));

    long activeLoans = loanRepository.countByMemberIdAndReturnedAtIsNull(member.getId());

    if (activeLoans >= maxActiveLoans) {
      throw new BusinessException(
          ErrorCode.MAX_ACTIVE_LOANS_REACHED,
          ErrorMessage.MAX_ACTIVE_LOANS_REACHED,
          HttpStatus.CONFLICT);
    }

    Instant now = Instant.now();

    if (loanRepository.existsByMemberIdAndReturnedAtIsNullAndDueDateBefore(member.getId(), now)) {
      throw new BusinessException(
          ErrorCode.MEMBER_HAS_OVERDUE_LOAN,
          ErrorMessage.MEMBER_HAS_OVERDUE_LOAN,
          HttpStatus.CONFLICT);
    }

    BookEntity book =
        bookRepository
            .findByIdForUpdate(request.getBookId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        ErrorCode.BOOK_NOT_FOUND,
                        ErrorMessage.BOOK_NOT_FOUND,
                        HttpStatus.NOT_FOUND));

    if (book.getAvailableCopies() <= 0) {
      throw new BusinessException(
          ErrorCode.BOOK_NOT_AVAILABLE, ErrorMessage.BOOK_NOT_AVAILABLE, HttpStatus.CONFLICT);
    }

    book.setAvailableCopies(book.getAvailableCopies() - 1);
    book.setUpdatedAt(now);

    LoanEntity loan = new LoanEntity();
    loan.setBook(book);
    loan.setMember(member);
    loan.setBorrowedAt(now);
    loan.setDueDate(now.plus(loanDurationDays, ChronoUnit.DAYS));
    loan.setCreatedAt(now);
    loan.setUpdatedAt(now);

    bookRepository.save(book);
    loan = loanRepository.save(loan);

    log.info(
        "[LoanServiceImpl][borrow] Book borrowed: loanId={}, bookId={}, memberId={}",
        loan.getId(),
        book.getId(),
        member.getId());

    return LoanResponse.builder()
        .id(loan.getId())
        .bookId(book.getId())
        .memberId(member.getId())
        .borrowedAt(loan.getBorrowedAt())
        .dueDate(loan.getDueDate())
        .returnedAt(loan.getReturnedAt())
        .build();
  }

  @Override
  @Transactional
  public LoanResponse returnBook(Long loanId) {
    LoanEntity loan =
        loanRepository
            .findById(loanId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        ErrorCode.LOAN_NOT_FOUND,
                        ErrorMessage.LOAN_NOT_FOUND,
                        HttpStatus.NOT_FOUND));

    if (loan.getReturnedAt() != null) {
      throw new BusinessException(
          ErrorCode.LOAN_ALREADY_RETURNED, ErrorMessage.LOAN_ALREADY_RETURNED, HttpStatus.CONFLICT);
    }

    BookEntity book =
        bookRepository
            .findByIdForUpdate(loan.getBook().getId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        ErrorCode.BOOK_NOT_FOUND,
                        ErrorMessage.BOOK_NOT_FOUND,
                        HttpStatus.NOT_FOUND));

    Instant now = Instant.now();

    loan.setReturnedAt(now);
    loan.setUpdatedAt(now);

    book.setAvailableCopies(book.getAvailableCopies() + 1);
    book.setUpdatedAt(now);

    bookRepository.save(book);
    loan = loanRepository.save(loan);

    log.info(
        "[LoanServiceImpl][returnBook] return data: loanId={}, bookId={}, memberId={}",
        loan.getId(),
        book.getId(),
        loan.getMember().getId());

    return LoanResponse.builder()
        .id(loan.getId())
        .bookId(book.getId())
        .memberId(loan.getMember().getId())
        .borrowedAt(loan.getBorrowedAt())
        .dueDate(loan.getDueDate())
        .returnedAt(loan.getReturnedAt())
        .build();
  }
}
