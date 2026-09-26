package com.booklending.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.booklending.common.exception.BusinessException;
import com.booklending.dto.book.BookResponse;
import com.booklending.dto.book.CreateBookRequest;
import com.booklending.dto.book.UpdateBookRequest;
import com.booklending.entity.BookEntity;
import com.booklending.repository.BookRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

  @Mock private BookRepository bookRepository;

  @InjectMocks private BookServiceImpl bookService;

  @Test
  void test_create_success() {
    CreateBookRequest request = new CreateBookRequest();
    request.setTitle("Clean Code");
    request.setAuthor("Robert C. Martin");
    request.setIsbn("9780132350884");
    request.setTotalCopies(5);

    when(bookRepository.existsByIsbn(request.getIsbn())).thenReturn(false);
    when(bookRepository.save(any(BookEntity.class)))
        .thenAnswer(
            invocation -> {
              BookEntity book = invocation.getArgument(0);
              book.setId(1L);
              return book;
            });

    BookResponse response = bookService.create(request);

    assertEquals(1L, response.getId());
    assertEquals("Clean Code", response.getTitle());
    assertEquals(5, response.getTotalCopies());
    assertEquals(5, response.getAvailableCopies());
  }

  @Test
  void test_create_duplicateIsbn() {
    CreateBookRequest request = new CreateBookRequest();
    request.setIsbn("9780132350884");

    when(bookRepository.existsByIsbn(request.getIsbn())).thenReturn(true);

    assertThrows(BusinessException.class, () -> bookService.create(request));

    verify(bookRepository, never()).save(any());
  }

  @Test
  void test_getById_success() {
    BookEntity book = createBook();

    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

    BookResponse response = bookService.getById(1L);

    assertEquals(1L, response.getId());
    assertEquals("Clean Code", response.getTitle());
  }

  @Test
  void test_getById_notFound() {
    when(bookRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> bookService.getById(99L));
  }

  @Test
  void test_update_success() {
    BookEntity book = createBook();
    book.setAvailableCopies(3);

    UpdateBookRequest request = new UpdateBookRequest();
    request.setTitle("Clean Code Updated");
    request.setAuthor("Robert C. Martin");
    request.setIsbn("9780132350884");
    request.setTotalCopies(6);

    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

    when(bookRepository.save(any(BookEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    BookResponse response = bookService.update(1L, request);

    assertEquals("Clean Code Updated", response.getTitle());
    assertEquals(6, response.getTotalCopies());
    assertEquals(4, response.getAvailableCopies());
  }

  @Test
  void test_update_invalidTotalCopies() {
    BookEntity book = createBook();
    book.setAvailableCopies(2);

    UpdateBookRequest request = new UpdateBookRequest();
    request.setTitle("Clean Code");
    request.setAuthor("Robert C. Martin");
    request.setIsbn("9780132350884");
    request.setTotalCopies(2);

    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

    assertThrows(BusinessException.class, () -> bookService.update(1L, request));

    verify(bookRepository, never()).save(any());
  }

  private BookEntity createBook() {
    BookEntity book = new BookEntity();
    book.setId(1L);
    book.setTitle("Clean Code");
    book.setAuthor("Robert C. Martin");
    book.setIsbn("9780132350884");
    book.setTotalCopies(5);
    book.setAvailableCopies(5);
    return book;
  }
}
