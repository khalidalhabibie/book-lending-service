package com.booklending.service.impl;

import com.booklending.common.exception.BusinessException;
import com.booklending.common.exception.ErrorCode;
import com.booklending.common.exception.ErrorMessage;
import com.booklending.dto.member.CreateMemberRequest;
import com.booklending.dto.member.MemberResponse;
import com.booklending.dto.member.UpdateMemberRequest;
import com.booklending.entity.MemberEntity;
import com.booklending.repository.MemberRepository;
import com.booklending.service.MemberService;
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
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;

  @Override
  @Transactional
  public MemberResponse create(CreateMemberRequest request) {
    if (memberRepository.existsByEmail(request.getEmail())) {
      throw new BusinessException(
          ErrorCode.DUPLICATE_EMAIL, ErrorMessage.DUPLICATE_EMAIL, HttpStatus.CONFLICT);
    }

    MemberEntity member = new MemberEntity();
    member.setName(request.getName());
    member.setEmail(request.getEmail());
    member.setCreatedAt(Instant.now());
    member.setUpdatedAt(Instant.now());

    member = memberRepository.save(member);

    log.info("[MemberServiceImpl][create] Member created: memberId={}", member.getId());

    return MemberResponse.builder()
        .id(member.getId())
        .name(member.getName())
        .email(member.getEmail())
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public MemberResponse getById(Long id) {
    MemberEntity member =
        memberRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new BusinessException(
                        ErrorCode.MEMBER_NOT_FOUND,
                        ErrorMessage.MEMBER_NOT_FOUND,
                        HttpStatus.NOT_FOUND));

    return MemberResponse.builder()
        .id(member.getId())
        .name(member.getName())
        .email(member.getEmail())
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public List<MemberResponse> getAll() {
    return memberRepository.findAll().stream()
        .map(
            member ->
                MemberResponse.builder()
                    .id(member.getId())
                    .name(member.getName())
                    .email(member.getEmail())
                    .build())
        .toList();
  }

  @Override
  @Transactional
  public MemberResponse update(Long id, UpdateMemberRequest request) {
    MemberEntity member =
        memberRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new BusinessException(
                        ErrorCode.MEMBER_NOT_FOUND,
                        ErrorMessage.MEMBER_NOT_FOUND,
                        HttpStatus.NOT_FOUND));

    if (!member.getEmail().equals(request.getEmail())
        && memberRepository.existsByEmail(request.getEmail())) {
      throw new BusinessException(
          ErrorCode.DUPLICATE_EMAIL, ErrorMessage.DUPLICATE_EMAIL, HttpStatus.CONFLICT);
    }

    member.setName(request.getName());
    member.setEmail(request.getEmail());
    member.setUpdatedAt(Instant.now());

    member = memberRepository.save(member);

    log.info("[MemberServiceImpl][update] Member updated: memberId={}", member.getId());

    return MemberResponse.builder()
        .id(member.getId())
        .name(member.getName())
        .email(member.getEmail())
        .build();
  }
}
