package com.booklending.dto.book;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookResponse {

  private Long id;
  private String title;
  private String author;
  private String isbn;
  private Integer totalCopies;
  private Integer availableCopies;
}
