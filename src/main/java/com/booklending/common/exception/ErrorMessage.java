package com.booklending.common.exception;

public final class ErrorMessage {

  private ErrorMessage() {}

  public static final String BOOK_NOT_FOUND = "Book not found";
  public static final String DUPLICATE_ISBN = "Book with this ISBN already exists";
  public static final String INVALID_TOTAL_COPIES =
      "Total copies cannot be less than borrowed copies";

  public static final String MEMBER_NOT_FOUND = "Member not found";
  public static final String DUPLICATE_EMAIL = "Member with this email already exists";

  public static final String LOAN_NOT_FOUND = "Loan not found";
  public static final String BOOK_NOT_AVAILABLE = "Book is not available";
  public static final String MAX_ACTIVE_LOANS_REACHED = "Maximum active loans reached";
  public static final String MEMBER_HAS_OVERDUE_LOAN = "Member has an overdue loan";
  public static final String LOAN_ALREADY_RETURNED = "Loan has already been returned";
}
