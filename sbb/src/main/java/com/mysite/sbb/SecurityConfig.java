package com.mysite.sbb;

import java.io.IOException;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import com.mysite.sbb.naver.NaverUserController;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//@ComponentScan("controllers")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
	
	private final CustomLogInSuccessHandler customLogInSuccessHandler;
	

	public SecurityConfig(CustomLogInSuccessHandler customLogInSuccessHandler) {
	    this.customLogInSuccessHandler = customLogInSuccessHandler;
	  
	}

	 @Autowired
	    private OauthSuccessHandler oauthSuccessHandler;

	
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http 	
        	
            .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                .requestMatchers(new AntPathRequestMatcher("/**")).permitAll()
                //.requestMatchers(new AntPathRequestMatcher("/user/naverLogin")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/login/oauth2/code/naver")).permitAll()
                .anyRequest().authenticated()		)

            .csrf((csrf) -> csrf
                .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")))
//            .csrf(csrf -> csrf.disable())
            .headers((headers) -> headers
                    .addHeaderWriter(new XFrameOptionsHeaderWriter(
                        XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
            .formLogin((formLogin) -> formLogin
            		.loginPage("/user/login")
            		.defaultSuccessUrl("/")
            		.successHandler(customLogInSuccessHandler)) 
            .oauth2Login((oauth2Login) -> oauth2Login
            	
            		  // .defaultSuccessUrl("/")
            		.successHandler(customLogInSuccessHandler)
            		.permitAll()) 
            .logout((logout)-> logout
            		.logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
            		.logoutSuccessUrl("/")                                                                                                                                                                                                                                                                                                                                                                                   
            		.invalidateHttpSession(true));
   

     
        
        return http.build();
    }
    
    
    
    @Bean
    PasswordEncoder passwordEncoder() {
    	return new BCryptPasswordEncoder();
    }
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

 
}    
