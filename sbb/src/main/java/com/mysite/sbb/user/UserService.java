package com.mysite.sbb.user;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.IncorrectPasswordException;
import com.mysite.sbb.UserNotFoundException;
import com.mysite.sbb.naver.NaverLoginService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;



@RequiredArgsConstructor
@Service
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final NaverLoginService naverUserService;
	
	public SiteUser create(String username, String email, String password) {
		SiteUser user = new SiteUser();
		user.setUserName(username);
		user.setEmail(email);
	    String encryptedPassword = bCryptPasswordEncoder.encode(password); // 비밀번호를 암호화
	    user.setPassword(encryptedPassword);
		this.userRepository.save(user);
		return user;
	}
	
	public SiteUser getUser(String username) {
		Optional<SiteUser> siteUser = this.userRepository.findByUserName(username);
		if(siteUser.isPresent()) {
			return siteUser.get();
		} else {
			throw new DataNotFoundException("siteuser not found");
		}
	}
	
   public String findEmail(String email) {
      Optional<SiteUser> _siteUser = this.userRepository.findByEmail(email);
      if (!_siteUser.isPresent()) {
         throw new UsernameNotFoundException("사용자를 찾을수 없습니다.");
      }
      String username = _siteUser.get().getUserName();
      return username;
   }
   public String getEmail(String email) {
	   Optional<SiteUser> siteUser = this.userRepository.findByEmail(email);
	   if (siteUser.isPresent()) {
		   return siteUser.get().getEmail();
	   } else {
		   throw new DataNotFoundException("siteuser not found");
	   }
   }
   // 임시 비번이 맞는지 확인
   public boolean checkPassword(String username, String password) {
	    Optional<SiteUser> siteUserOptional = this.userRepository.findByUserName(username);
	    if (siteUserOptional.isPresent()) {
	        SiteUser siteUser = siteUserOptional.get();
	        String storedPassword = siteUser.getPassword();
	        if (bCryptPasswordEncoder.matches(password, storedPassword)) {
	            return true;
	        } else {
	            throw new IncorrectPasswordException("비밀번호가 일치하지 않습니다.");
	        }
	    } else {
	        throw new UserNotFoundException("해당 사용자 이름으로 등록된 사용자가 없습니다.");
	    }
	}
   // 프로필 사진 업로드 로직
   private static final String UPLOAD_DIR = "src/main/resources/static/";
   
   public String saveProfileImage(String userName, MultipartFile file) throws Exception {
	    // 파일 저장 로직 구현
	    // 해당 파일의 경로 반환
	    try {
	    
	        Path uploadPath = Paths.get(UPLOAD_DIR);
	        if (!Files.exists(uploadPath) || !Files.isDirectory(uploadPath)) {
	            Files.createDirectories(uploadPath);
	        }

	        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

	        Path filePath = uploadPath.resolve(fileName); // resolve: 합치기
	        System.out.println("filePath: " + filePath);
	        fileName = "/" + fileName;
	        System.out.println("fileName: " + fileName);
	        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING); // 원래는 uuid로 파일을 복사함
	        // db에 저장
	        Optional<SiteUser> user = this.userRepository.findByUserName(userName);
	        user.get().setProfilePath(fileName);
	 
	        this.userRepository.save(user.get());
	        System.out.println("filePath.toString():" + filePath.toString());
	        
	         return fileName;
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	        throw new RuntimeException("프로필 이미지를 저장하는 중에 오류가 발생했습니다.");
	    }
	}

   
  public String getProfileImagePath(String username) { 
	  Optional<SiteUser> user = this.userRepository.findByUserName(username);
	  String profilePath = user.get().getProfilePath();
	  return profilePath;
  }
  
  // Login with Naver
    private final ApplicationEnvironmentConfig envConfig;

    public String getNaverAuthorizeUrl(String type) throws URISyntaxException, MalformedURLException, UnsupportedEncodingException {

        String baseUrl = "https://nid.naver.com/oauth2.0/authorize";
        String clientId = envConfig.getNaverClientId();
        String redirectUrl = envConfig.getNaverRedirectUrl();

        String encodedRedirectUrl = URLEncoder.encode(redirectUrl, "UTF-8");
        String encodedState = URLEncoder.encode("1234", "UTF-8");
        
        UriComponents uriComponents = UriComponentsBuilder
                .fromUriString(baseUrl)
          
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", encodedRedirectUrl)
                .queryParam("state", encodedState) // state는 필요에 따라 수정할 수 있음
                .build();
	        return uriComponents.toUriString();
	    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<SiteUser> userOptional = userRepository.findByUserName(username); 
        SiteUser user = userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserName()) // 
                .password("")
                .authorities("ROLE_USER")
                .build();
    }
    public Optional<SiteUser> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
}
