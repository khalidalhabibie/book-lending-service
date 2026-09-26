package com.booklending.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.booklending.common.exception.BusinessException;
import com.booklending.dto.loan.BorrowBookRequest;
import com.booklending.dto.loan.LoanResponse;
import com.booklending.entity.BookEntity;
import com.booklending.entity.LoanEntity;
import com.booklending.entity.MemberEntity;
import com.booklending.repository.BookRepository;
import com.booklending.repository.LoanRepository;
import com.booklending.repository.MemberRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

  @Mock private LoanRepository loanRepository;

  @Mock private BookRepository bookRepository;

  @Mock private MemberRepository memberRepository;

  @InjectMocks private LoanServiceImpl loanService;

  private MemberEntity member;
  private BookEntity book;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(loanService, "maxActiveLoans", 5);
    ReflectionTestUtils.setField(loanService, "loanDurationDays", 14);

    member = new MemberEntity();
    member.setId(1L);
    member.setName("Khalid");
    member.setEmail("khalid@example.com");

    book = new BookEntity();
    book.setId(1L);
    book.setTitle("Clean Code");
    book.setAuthor("Robert C. Martin");
    book.setIsbn("9780132350884");
    book.setTotalCopies(5);
    book.setAvailableCopies(5);
  }

  @Test
  void test_borrow_success() {
    BorrowBookRequest request = new BorrowBookRequest();
    request.setMemberId(1L);
    request.setBookId(1L);

    when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

    when(loanRepository.countByMemberIdAndReturnedAtIsNull(1L)).thenReturn(0L);

    when(loanRepository.existsByMemberIdAndReturnedAtIsNullAndDueDateBefore(
            eq(1L), any(Instant.class)))
        .thenReturn(false);

    when(bookRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(book));

    when(loanRepository.save(any(LoanEntity.class)))
        .thenAnswer(
            invocation -> {
              LoanEntity loan = invocation.getArgument(0);
              loan.setId(1L);
              return loan;
            });

    LoanResponse response = loanService.borrow(request);

    assertEquals(1L, response.getId());
    assertEquals(1L, response.getBookId());
    assertEquals(1L, response.getMemberId());
    assertEquals(4, book.getAvailableCopies());

    verify(loanRepository).save(any(LoanEntity.class));
  }

  @Test
  void test_borrow_bookUnavailable() {
    book.setAvailableCopies(0);

    BorrowBookRequest request = new BorrowBookRequest();
    request.setMemberId(1L);
    request.setBookId(1L);

    when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

    when(loanRepository.countByMemberIdAndReturnedAtIsNull(1L)).thenReturn(0L);

    when(loanRepository.existsByMemberIdAndReturnedAtIsNullAndDueDateBefore(
            eq(1L), any(Instant.class)))
        .thenReturn(false);

    when(bookRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(book));

    assertThrows(BusinessException.class, () -> loanService.borrow(request));

    verify(loanRepository, never()).save(any());
  }

  @Test
  void test_borrow_maxLoansReached() {
    BorrowBookRequest request = new BorrowBookRequest();
    request.setMemberId(1L);
    request.setBookId(1L);

    when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

    when(loanRepository.countByMemberIdAndReturnedAtIsNull(1L)).thenReturn(5L);

    assertThrows(BusinessException.class, () -> loanService.borrow(request));

    verify(bookRepository, never()).findByIdForUpdate(any());
  }

  @Test
  void test_borrow_overdueLoan() {
    BorrowBookRequest request = new BorrowBookRequest();
    request.setMemberId(1L);
    request.setBookId(1L);

    when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

    when(loanRepository.countByMemberIdAndReturnedAtIsNull(1L)).thenReturn(1L);

    when(loanRepository.existsByMemberIdAndReturnedAtIsNullAndDueDateBefore(
            eq(1L), any(Instant.class)))
        .thenReturn(true);

    assertThrows(BusinessException.class, () -> loanService.borrow(request));

    verify(bookRepository, never()).findByIdForUpdate(any());
  }

  @Test
  void test_returnBook_success() {
    book.setAvailableCopies(4);

    LoanEntity loan = new LoanEntity();
    loan.setId(1L);
    loan.setBook(book);
    loan.setMember(member);
    loan.setBorrowedAt(Instant.now().minusSeconds(3600));
    loan.setDueDate(Instant.now().plusSeconds(3600));

    when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

    when(bookRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(book));

    when(loanRepository.save(any(LoanEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    LoanResponse response = loanService.returnBook(1L);

    assertNotNull(response.getReturnedAt());
    assertEquals(5, book.getAvailableCopies());

    verify(loanRepository).save(loan);
    verify(bookRepository).save(book);
  }

  @Test
  void test_returnBook_alreadyReturned() {
    LoanEntity loan = new LoanEntity();
    loan.setId(1L);
    loan.setBook(book);
    loan.setMember(member);
    loan.setReturnedAt(Instant.now());

    when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

    assertThrows(BusinessException.class, () -> loanService.returnBook(1L));

    verify(bookRepository, never()).findByIdForUpdate(any());
    verify(loanRepository, never()).save(any());
  }
}
