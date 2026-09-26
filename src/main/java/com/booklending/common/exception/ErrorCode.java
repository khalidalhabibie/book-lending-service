package com.booklending.common.exception;

public final class ErrorCode {

  private ErrorCode() {}

  public static final String BOOK_NOT_FOUND = "BOOK_001";
  public static final String DUPLICATE_ISBN = "BOOK_002";
  public static final String INVALID_TOTAL_COPIES = "BOOK_003";

  public static final String MEMBER_NOT_FOUND = "MEMBER_001";
  public static final String DUPLICATE_EMAIL = "MEMBER_002";

  public static final String LOAN_NOT_FOUND = "LOAN_001";
  public static final String BOOK_NOT_AVAILABLE = "LOAN_002";
  public static final String MAX_ACTIVE_LOANS_REACHED = "LOAN_003";
  public static final String MEMBER_HAS_OVERDUE_LOAN = "LOAN_004";
  public static final String LOAN_ALREADY_RETURNED = "LOAN_005";
}
