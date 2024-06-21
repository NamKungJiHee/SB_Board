package com.mysite.sbb.user;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPwdForm {
	@NotEmpty(message="사용자이름은 필수 항목입니다")
	private String username;
	
	@NotEmpty(message="현재 비밀번호는 필수 항목입니다")
	private String password;
	
	@NotEmpty(message="새 비밀번호는 필수 항목입니다")
	private String newPassword1;
	
	@NotEmpty(message="새 비밀번호 확인은 필수 항목입니다")
	private String newPassword2;
}


	
