package com.mysite.sbb.question;

import com.mysite.sbb.user.SiteUser;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import com.mysite.sbb.answer.Answer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.CascadeType;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@SequenceGenerator(name="question_seq", sequenceName="question_seq", initialValue=1, allocationSize=1)
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="question_seq")
    private Integer questionId;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;
    
    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;
    
    @ManyToOne
    private SiteUser author;
    
    private LocalDateTime modifyDate;
    
    @ManyToMany
    Set<SiteUser> voter;
    
    @Column(columnDefinition = "integer default 0", nullable = false)
    private int viewer;
}
