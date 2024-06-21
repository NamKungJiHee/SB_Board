package com.mysite.sbb.lecture;

import org.springframework.stereotype.Service;

import com.mysite.sbb.lecture.LectureRepository;
import com.mysite.sbb.question.QuestionRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class LectureService {
	private final LectureRepository lectureRepository;
	
	
}
