package com.booklending.controller;

import com.booklending.common.response.ApiResponse;
import com.booklending.dto.book.BookResponse;
import com.booklending.dto.book.CreateBookRequest;
import com.booklending.dto.book.UpdateBookRequest;
import com.booklending.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookResponse>> create(
            @Valid @RequestBody CreateBookRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(bookService.create(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success(bookService.getAll())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(bookService.getById(id))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(bookService.update(id, request))
        );
    }
}