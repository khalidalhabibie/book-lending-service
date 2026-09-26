package com.booklending.service.impl;

import com.booklending.common.exception.BusinessException;
import com.booklending.common.exception.ErrorCode;
import com.booklending.common.exception.ErrorMessage;
import com.booklending.dto.book.BookResponse;
import com.booklending.dto.book.CreateBookRequest;
import com.booklending.dto.book.UpdateBookRequest;
import com.booklending.entity.BookEntity;
import com.booklending.repository.BookRepository;
import com.booklending.service.BookService;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

  private final BookRepository bookRepository;

  @Override
  @Transactional
  public BookResponse create(CreateBookRequest request) {
    if (bookRepository.existsByIsbn(request.getIsbn())) {
      throw new BusinessException(
          ErrorCode.DUPLICATE_ISBN, ErrorMessage.DUPLICATE_ISBN, HttpStatus.CONFLICT);
    }

    BookEntity book = new BookEntity();
    book.setTitle(request.getTitle());
    book.setAuthor(request.getAuthor());
    book.setIsbn(request.getIsbn());
    book.setTotalCopies(request.getTotalCopies());
    book.setAvailableCopies(request.getTotalCopies());
    book.setCreatedAt(Instant.now());
    book.setUpdatedAt(Instant.now());

    book = bookRepository.save(book);

    log.info(
        "[BookServiceImpl][create] Book created: bookId={}, isbn={}", book.getId(), book.getIsbn());

    return BookResponse.builder()
        .id(book.getId())
        .title(book.getTitle())
        .author(book.getAuthor())
        .isbn(book.getIsbn())
        .totalCopies(book.getTotalCopies())
        .availableCopies(book.getAvailableCopies())
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public BookResponse getById(Long id) {
    BookEntity book =
        bookRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new BusinessException(
                        ErrorCode.BOOK_NOT_FOUND,
                        ErrorMessage.BOOK_NOT_FOUND,
                        HttpStatus.NOT_FOUND));

    return BookResponse.builder()
        .id(book.getId())
        .title(book.getTitle())
        .author(book.getAuthor())
        .isbn(book.getIsbn())
        .totalCopies(book.getTotalCopies())
        .availableCopies(book.getAvailableCopies())
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public List<BookResponse> getAll() {
    return bookRepository.findAll().stream()
        .map(
            book ->
                BookResponse.builder()
                    .id(book.getId())
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .isbn(book.getIsbn())
                    .totalCopies(book.getTotalCopies())
                    .availableCopies(book.getAvailableCopies())
                    .build())
        .toList();
  }

  @Override
  @Transactional
  public BookResponse update(Long id, UpdateBookRequest request) {
    BookEntity book =
        bookRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new BusinessException(
                        ErrorCode.BOOK_NOT_FOUND,
                        ErrorMessage.BOOK_NOT_FOUND,
                        HttpStatus.NOT_FOUND));

    if (!book.getIsbn().equals(request.getIsbn())
        && bookRepository.existsByIsbn(request.getIsbn())) {
      throw new BusinessException(
          ErrorCode.DUPLICATE_ISBN, ErrorMessage.DUPLICATE_ISBN, HttpStatus.CONFLICT);
    }

    int borrowedCopies = book.getTotalCopies() - book.getAvailableCopies();

    if (request.getTotalCopies() < borrowedCopies) {
      throw new BusinessException(
          ErrorCode.INVALID_TOTAL_COPIES, ErrorMessage.INVALID_TOTAL_COPIES, HttpStatus.CONFLICT);
    }

    book.setTitle(request.getTitle());
    book.setAuthor(request.getAuthor());
    book.setIsbn(request.getIsbn());
    book.setTotalCopies(request.getTotalCopies());
    book.setAvailableCopies(request.getTotalCopies() - borrowedCopies);
    book.setUpdatedAt(Instant.now());

    book = bookRepository.save(book);

    log.info(
        "[BookServiceImpl][update] Book updated: bookId={}, isbn={}", book.getId(), book.getIsbn());

    return BookResponse.builder()
        .id(book.getId())
        .title(book.getTitle())
        .author(book.getAuthor())
        .isbn(book.getIsbn())
        .totalCopies(book.getTotalCopies())
        .availableCopies(book.getAvailableCopies())
        .build();
  }
}
