package com.mysite.sbb.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<SiteUser, Long> {
	Optional<SiteUser> findByEmail(String email);
	Optional<SiteUser> findByUserName(String userName);
	Optional<SiteUser> findByUserId(Long userId); 
	Optional<SiteUser> findByPassword(String password); 
}
