package com.booklending.service;

import com.booklending.dto.member.CreateMemberRequest;
import com.booklending.dto.member.MemberResponse;
import com.booklending.dto.member.UpdateMemberRequest;
import java.util.List;

public interface MemberService {

  MemberResponse create(CreateMemberRequest request);

  MemberResponse getById(Long id);

  List<MemberResponse> getAll();

  MemberResponse update(Long id, UpdateMemberRequest request);
}
