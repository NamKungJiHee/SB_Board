package com.mysite.sbb.answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface AnswerRepository extends JpaRepository<Answer, Integer>{
 
}
