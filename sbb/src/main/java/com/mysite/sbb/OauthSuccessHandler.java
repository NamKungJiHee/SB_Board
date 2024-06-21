package com.mysite.sbb;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@Component
public class OauthSuccessHandler implements AuthenticationSuccessHandler {
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
       System.out.println("########111");
    	OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String username = oAuth2User.getAttribute("name");
        // 사용자의 프로필 이미지 경로 가져오기
        String imagePath = getImagePathForUser(username);
        // 세션에 프로필 이미지 경로 저장
        request.getSession().setAttribute("profileImage", imagePath);
        // 로그인 성공 후 리다이렉트할 URL
        String targetUrl = determineTargetUrl(request, response);
        // 리다이렉트
        response.sendRedirect(targetUrl);
    }

    private String getImagePathForUser(String username) {
        // 사용자의 프로필 이미지 경로를 가져오는 로직을 구현
        return "/profile.jpg"; // 임시로 "/profile.jpg"로 설정
    }

    private String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        // 로그인 성공 후 리다이렉트할 URL을 정의
        return "/";
    }
}
