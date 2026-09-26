package com.booklending.service;

import com.booklending.dto.book.*;
import com.booklending.dto.book.CreateBookRequest;
import com.booklending.dto.book.UpdateBookRequest;
import java.util.List;

public interface BookService {

  BookResponse create(CreateBookRequest request);

  BookResponse getById(Long id);

  List<BookResponse> getAll();

  BookResponse update(Long id, UpdateBookRequest request);
}
