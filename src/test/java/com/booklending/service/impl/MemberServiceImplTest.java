package com.booklending.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.booklending.common.exception.BusinessException;
import com.booklending.dto.member.CreateMemberRequest;
import com.booklending.dto.member.MemberResponse;
import com.booklending.dto.member.UpdateMemberRequest;
import com.booklending.entity.MemberEntity;
import com.booklending.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

  @Mock private MemberRepository memberRepository;

  @InjectMocks private MemberServiceImpl memberService;

  @Test
  void test_create_success() {
    CreateMemberRequest request = new CreateMemberRequest();
    request.setName("Khalid");
    request.setEmail("khalid@example.com");

    when(memberRepository.existsByEmail(request.getEmail())).thenReturn(false);

    when(memberRepository.save(any(MemberEntity.class)))
        .thenAnswer(
            invocation -> {
              MemberEntity member = invocation.getArgument(0);
              member.setId(1L);
              return member;
            });

    MemberResponse response = memberService.create(request);

    assertEquals(1L, response.getId());
    assertEquals("Khalid", response.getName());
    assertEquals("khalid@example.com", response.getEmail());
  }

  @Test
  void test_create_duplicateEmail() {
    CreateMemberRequest request = new CreateMemberRequest();
    request.setName("Khalid");
    request.setEmail("khalid@example.com");

    when(memberRepository.existsByEmail(request.getEmail())).thenReturn(true);

    assertThrows(BusinessException.class, () -> memberService.create(request));

    verify(memberRepository, never()).save(any());
  }

  @Test
  void test_getById_success() {
    MemberEntity member = createMember();

    when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

    MemberResponse response = memberService.getById(1L);

    assertEquals(1L, response.getId());
    assertEquals("Khalid", response.getName());
    assertEquals("khalid@example.com", response.getEmail());
  }

  @Test
  void test_getById_notFound() {
    when(memberRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> memberService.getById(99L));
  }

  @Test
  void test_update_success() {
    MemberEntity member = createMember();

    UpdateMemberRequest request = new UpdateMemberRequest();
    request.setName("Khalid Alhabibie");
    request.setEmail("khalid.new@example.com");

    when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

    when(memberRepository.existsByEmail(request.getEmail())).thenReturn(false);

    when(memberRepository.save(any(MemberEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    MemberResponse response = memberService.update(1L, request);

    assertEquals("Khalid Alhabibie", response.getName());
    assertEquals("khalid.new@example.com", response.getEmail());
  }

  @Test
  void test_update_duplicateEmail() {
    MemberEntity member = createMember();

    UpdateMemberRequest request = new UpdateMemberRequest();
    request.setName("Khalid");
    request.setEmail("other@example.com");

    when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

    when(memberRepository.existsByEmail(request.getEmail())).thenReturn(true);

    assertThrows(BusinessException.class, () -> memberService.update(1L, request));

    verify(memberRepository, never()).save(any());
  }

  private MemberEntity createMember() {
    MemberEntity member = new MemberEntity();
    member.setId(1L);
    member.setName("Khalid");
    member.setEmail("khalid@example.com");
    return member;
  }
}
