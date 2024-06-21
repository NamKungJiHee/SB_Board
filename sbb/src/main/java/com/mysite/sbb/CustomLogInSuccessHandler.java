package com.mysite.sbb;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.mysite.sbb.naver.NaverLoginService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Component
public class CustomLogInSuccessHandler implements AuthenticationSuccessHandler {

    private final ImagePathService imagePathService;
    private final NaverLoginService naverLoginService;
    
    public CustomLogInSuccessHandler(ImagePathService imagePathService,NaverLoginService naverLoginService) { 
        this.imagePathService = imagePathService;
        this.naverLoginService = naverLoginService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String username = null;
        String imagePath = null;
        String email = null;

        if (authentication.getPrincipal() instanceof OAuth2User) {
            // (1) OAuth2 로그인인 경우(Naver)
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = oauth2User.getAttributes();
            Map<String, Object> responseMap = (Map<String, Object>) attributes.get("response");

            email = (String) responseMap.get("email");
            System.out.println("EMAIL: " + email);
            username = (String) responseMap.get("name");
            System.out.println("USERNAME: " + username);
            // 프로필 이미지 경로 가져오기
            imagePath = getImagePathForUser(username);
            System.out.println("IMAGEPATH: " + imagePath);

            // 새로운 사용자인 경우에만 저장
            naverLoginService.saveUserIfNotExist(email, username);
            // UserDetails를 구현한 User 객체를 생성하고 이름을 설정
            UserDetails userDetails = User.withUsername(username)
                    .password("") 
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                    .build();

         // 변경된 사용자 정보를 Authentication 객체에 설정
            Authentication newAuth = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(newAuth);
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            // (2) 일반적인 로그인인 경우
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
            imagePath = getImagePathForUser(username);
        }

   
        if (imagePath == null) {
            imagePath = "/profile.jpg";
        }

        // 세션에 프로필 이미지 경로를 설정
        HttpSession session = request.getSession();
        session.setAttribute("profileImage", imagePath);

        // 로그인 성공 후 리다이렉트할 URL을 정의
        response.sendRedirect("/");
    }

    // 사용자 이름으로부터 프로필 이미지 경로 가져오기
    private String getImagePathForUser(String username) {
        return imagePathService.getProfileImagePath(username);
    }
}
