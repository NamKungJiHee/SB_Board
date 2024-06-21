package com.mysite.sbb.answer;

import com.mysite.sbb.user.SiteUser;
import java.time.LocalDateTime;
import java.util.Set;
import com.mysite.sbb.question.Question;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@SequenceGenerator(name="answer_seq", sequenceName="answer_seq", initialValue=1, allocationSize=1)
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="answer_seq")
    private Integer answerId;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate; 
    
    @ManyToOne
    private Question question;  
    
    @ManyToOne
    private SiteUser author;
    
    private LocalDateTime modifyDate;
    
    @ManyToMany
    Set<SiteUser> voter;
}
