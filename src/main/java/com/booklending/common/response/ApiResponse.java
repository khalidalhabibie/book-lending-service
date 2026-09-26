package com.booklending.common.response;

import java.time.Instant;

public record ApiResponse<T>(
    boolean success, String code, String message, T data, Instant timestamp) {

  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(true, null, "Success", data, Instant.now());
  }

  public static <T> ApiResponse<T> error(String code, String message) {
    return new ApiResponse<>(false, code, message, null, Instant.now());
  }
}
