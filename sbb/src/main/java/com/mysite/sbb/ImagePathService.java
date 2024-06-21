package com.mysite.sbb;

import java.util.Optional;


import org.springframework.stereotype.Service;

import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserRepository;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Service
public class ImagePathService {
	private final UserRepository userRepository;
	
	 public String getProfileImagePath(String username) { 
		  Optional<SiteUser> user = this.userRepository.findByUserName(username);
	
		  String profilePath = null;

	        if (user.isEmpty()) {
	        } else {
	            profilePath = user.get().getProfilePath();
	        }
	       
	        return profilePath;
	    }
}
