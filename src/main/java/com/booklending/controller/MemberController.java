package com.booklending.controller;

import com.booklending.common.response.ApiResponse;
import com.booklending.dto.member.CreateMemberRequest;
import com.booklending.dto.member.MemberResponse;
import com.booklending.dto.member.UpdateMemberRequest;
import com.booklending.service.MemberService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  @PostMapping
  public ResponseEntity<ApiResponse<MemberResponse>> create(
      @Valid @RequestBody CreateMemberRequest request) {

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(memberService.create(request)));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<MemberResponse>>> getAll() {
    return ResponseEntity.ok(ApiResponse.success(memberService.getAll()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<MemberResponse>> getById(@PathVariable Long id) {
    return ResponseEntity.ok(ApiResponse.success(memberService.getById(id)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<MemberResponse>> update(
      @PathVariable Long id, @Valid @RequestBody UpdateMemberRequest request) {

    return ResponseEntity.ok(ApiResponse.success(memberService.update(id, request)));
  }
}
