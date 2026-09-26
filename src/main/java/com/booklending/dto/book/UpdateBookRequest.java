package com.booklending.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBookRequest {

  @NotBlank private String title;

  @NotBlank private String author;

  @NotBlank private String isbn;

  @NotNull
  @Min(1)
  private Integer totalCopies;
}
