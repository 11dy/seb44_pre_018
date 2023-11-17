package com.e1i5.stackOverflow.member;


import com.e1i5.stackOverflow.auth.utils.CustomAuthorityUtils;
import com.e1i5.stackOverflow.member.entity.Member;
import com.e1i5.stackOverflow.member.repository.MemberRepository;
import com.e1i5.stackOverflow.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

//@SpringBootTest
@AutoConfigureMockMvc
public class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private CustomAuthorityUtils authorityUtils;


    @BeforeEach
    public void setUp() {
        // Mock 데이터 설정 예시
        Member existingMember  = new Member();
        existingMember.setMemberId(1L);
        existingMember.setEmail("test@example");
        existingMember.setPassword("test@example");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(existingMember));
        when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingMember));
        when(passwordEncoder.encode("test@example")).thenReturn("test@example");

    }

    @Test
    public void testSignupMember() {
        // 가짜 회원 데이터 생성
        Member newMember = new Member();
        newMember.setEmail("new@example.com");
        newMember.setPassword("test@example.com");

        // UserRepository findByEmail Mock 설정
        when(memberRepository.findByEmail("USER")).thenReturn(Optional.empty());

        // 가짜 역할 데이터 생성
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");

        // authorityUtils.createRoles Mock 설정
        when(authorityUtils.createRoles("new@example.com")).thenReturn(roles);

        // 서비스 메서드 호출
        Member savedMember = memberService.signupMember(newMember);

        // 예상 결과와 실제 결과 비교
        assertEquals("new@example.com", savedMember.getEmail());
        assertEquals("encodedPassword", savedMember.getPassword());
        assertEquals(1, savedMember.getRoles().size());
        assertEquals("ROLE_USER", savedMember.getRoles().get(0));
    }

    @Test
    public void testFindMemberById() {
        // 테스트할 서비스 메서드 호출
        Member foundMember = memberService.findMember(1L);

        // 예상 결과와 실제 결과 비교
        assertEquals(1L, foundMember.getMemberId());
        assertEquals("test@example", foundMember.getEmail());
    }

}
