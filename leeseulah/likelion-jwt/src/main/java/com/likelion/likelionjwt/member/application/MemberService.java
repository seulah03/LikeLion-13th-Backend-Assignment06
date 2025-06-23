package com.likelion.likelionjwt.member.application;

import com.likelion.likelionjwt.common.error.ErrorCode;
import com.likelion.likelionjwt.common.exception.BusinessException;
import com.likelion.likelionjwt.global.jwt.JwtTokenProvider;
import com.likelion.likelionjwt.member.api.dto.request.AdminJoinReqDto;
import com.likelion.likelionjwt.member.api.dto.request.MemberJoinReqDto;
import com.likelion.likelionjwt.member.api.dto.request.MemberLoginReqDto;
import com.likelion.likelionjwt.member.api.dto.response.MemberInfoResDto;
import com.likelion.likelionjwt.member.domain.Member;
import com.likelion.likelionjwt.member.domain.Role;
import com.likelion.likelionjwt.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입 (email, password, name)
    @Transactional
    public void join(MemberJoinReqDto memberJoinReqDto){
        if(memberRepository.existsByEmail(memberJoinReqDto.email())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS_EMAIL,
                ErrorCode.ALREADY_EXISTS_EMAIL.getMessage());
        }

        Member member = Member.builder()
            .email(memberJoinReqDto.email())
            .password(passwordEncoder.encode(memberJoinReqDto.password()))
            .name(memberJoinReqDto.name())
            .role(Role.ROLE_USER)
            .build();

        memberRepository.save(member);
    }

    @Transactional
    public void joinAdmin(AdminJoinReqDto adminJoinReqDto){
        if(memberRepository.existsByEmail(adminJoinReqDto.email())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS_EMAIL,
                ErrorCode.ALREADY_EXISTS_EMAIL.getMessage());
        }

        Member member = Member.builder()
            .email(adminJoinReqDto.email())
            .password(passwordEncoder.encode(adminJoinReqDto.password()))
            .name(adminJoinReqDto.name())
            .role(Role.ROLE_ADMIN)
            .build();

        memberRepository.save(member);
    }

    @Transactional
    public MemberInfoResDto login(MemberLoginReqDto memberLoginReqDto){

        Member member = memberRepository.findByEmail(memberLoginReqDto.email())
            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION,
                ErrorCode.MEMBER_NOT_FOUND_EXCEPTION.getMessage()));

        if(!passwordEncoder.matches(memberLoginReqDto.password(), member.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD,
                ErrorCode.INVALID_PASSWORD.getMessage());
        }

        String token = jwtTokenProvider.generateToken(member);

        return MemberInfoResDto.of(member, token);
    }

    @Transactional
    public List<Member> memberInfo() {
        List<Member> members = memberRepository.findAll();


        return members;
    }

}
