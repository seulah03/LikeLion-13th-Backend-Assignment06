package com.likelion.likelionjwt.member.api;

import com.likelion.likelionjwt.common.error.SuccessCode;
import com.likelion.likelionjwt.common.template.ApiResTemplate;
import com.likelion.likelionjwt.member.api.dto.request.AdminJoinReqDto;
import com.likelion.likelionjwt.member.api.dto.request.MemberJoinReqDto;
import com.likelion.likelionjwt.member.api.dto.request.MemberLoginReqDto;
import com.likelion.likelionjwt.member.api.dto.response.MemberInfoResDto;
import com.likelion.likelionjwt.member.application.MemberService;
import com.likelion.likelionjwt.member.domain.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public ApiResTemplate<String> join(@RequestBody @Valid MemberJoinReqDto memberJoinReqDto) {
        memberService.join(memberJoinReqDto);

        return ApiResTemplate.successWithNoContent(SuccessCode.MEMBER_JOIN_SUCCESS);
    }

    @PostMapping("/join/admin")
    public ApiResTemplate<String> joinAdmin(@RequestBody @Valid AdminJoinReqDto adminJoinReqDto) {
        memberService.joinAdmin(adminJoinReqDto);

        return ApiResTemplate.successWithNoContent(SuccessCode.MEMBER_JOIN_ADMIN_SUCCESS);
    }

    @PostMapping("/login")
    public ApiResTemplate<MemberInfoResDto> login(@RequestBody @Valid MemberLoginReqDto memberLoginReqDto) {

        MemberInfoResDto memberInfoResDto = memberService.login(memberLoginReqDto);

        return ApiResTemplate.successResponse(SuccessCode.MEMBER_LOGIN_SUCCESS, memberInfoResDto);
    }

    @GetMapping("/info")
    public ApiResTemplate<List<Member>> memberInfo() {

        List<Member> members = memberService.memberInfo();

        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, members);


    }

}
