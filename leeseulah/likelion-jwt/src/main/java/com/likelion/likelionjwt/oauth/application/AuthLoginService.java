package com.likelion.likelionjwt.oauth.application;

import com.google.gson.Gson;
import com.likelion.likelionjwt.global.jwt.JwtTokenProvider;
import com.likelion.likelionjwt.member.domain.Member;
import com.likelion.likelionjwt.member.domain.Role;
import com.likelion.likelionjwt.member.domain.repository.MemberRepository;
import com.likelion.likelionjwt.oauth.api.dto.Token;
import com.likelion.likelionjwt.oauth.api.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthLoginService {

//    // 컨트롤러에서 받은 authorization code와 소셜 로그인 id를 받아 콘솔에 출력
//    public void socialLogin(String code, String registrationId){
//        System.out.println("code = " + code);
//        System.out.println("registrationId = " + registrationId);
//    }

    @Value("${client-id}") // import 시 lombok으로 하면 안됨
    private String GOOGLE_CLIENT_ID;

    @Value("${client-secret}")
    private String GOOGLE_CLIENT_SECRET;

    // 구글 인증 코드를 엑세스 토큰으로 교환하는 API 주소
    private final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    // OAuth 인증 후 구글이 리디렉션할 URI
    private final String GOOGLE_REDIRECT_URI = "https://qkrehdwns.shop/login/oauth2/code/google";

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public String getGoogleAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> params = Map.of(
            "code", code,
            "scope", "https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email",
            "client_id", GOOGLE_CLIENT_ID,
            "client_secret", GOOGLE_CLIENT_SECRET,
            "redirect_uri", GOOGLE_REDIRECT_URI,
            "grant_type", "authorization_code"
        );

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(GOOGLE_TOKEN_URL, params, String.class);

        if(responseEntity.getStatusCode().is2xxSuccessful()) {
            String json = responseEntity.getBody();
            Gson gson = new Gson();

            return gson.fromJson(json, Token.class)
                .getAccessToken();
        }
        throw new RuntimeException("구글 엑세스 토큰을 가져오는데 실패했습니다.");
    }

    public Token loginOrSignUp(String googleAccessToken){
        UserInfo userInfo = getUserInfo(googleAccessToken);

        if(!userInfo.getVerifiedEmail()){
            throw new RuntimeException("이메일 인증이 되지 않은 유저입니다.");
        }

        Member member = memberRepository.findByEmail(userInfo.getEmail()).orElseGet(() ->
            memberRepository.save(Member.builder()
                .email(userInfo.getEmail())
                .name(userInfo.getName())
                .pictureUrl(userInfo.getPictureUrl())
                .role(Role.ROLE_USER)
                .build())
        );

        String jwt = jwtTokenProvider.generateToken(member);
        return new Token(jwt);
    }

    public UserInfo getUserInfo(String accessToken){
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + accessToken;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

        if(responseEntity.getStatusCode().is2xxSuccessful()) {
            String json = responseEntity.getBody();
            Gson gson = new Gson();
            return gson.fromJson(json, UserInfo.class);
        }

        throw new RuntimeException("유저 정보를 가져오는데 실패했습니다.");
    }
}
