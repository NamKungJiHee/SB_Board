package com.mysite.sbb.lecture;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mysite.sbb.lecture.LectureService;
import com.mysite.sbb.lecture.LectureRepository;

import lombok.RequiredArgsConstructor;

@RequestMapping("/user")
@RequiredArgsConstructor
@Controller
public class LectureController {
	
	@GetMapping("/lecture")
	public String lectureList() {
	return "lecture_list";
	}
}
