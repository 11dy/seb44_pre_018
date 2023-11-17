package com.e1i5.stackOverflow.comment.service;

import com.e1i5.stackOverflow.comment.dto.CommentDto;
import com.e1i5.stackOverflow.comment.entity.Comment;
import com.e1i5.stackOverflow.comment.mapper.CommentMapper;
import com.e1i5.stackOverflow.comment.repository.CommentRepository;
import com.e1i5.stackOverflow.dto.MultiResponseDto;
import com.e1i5.stackOverflow.member.entity.Member;
import com.e1i5.stackOverflow.member.service.MemberService;
import com.e1i5.stackOverflow.question.entity.Question;
import com.e1i5.stackOverflow.question.repository.QuestionRepository;
import com.e1i5.stackOverflow.question.service.QuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static com.e1i5.stackOverflow.comment.entity.Comment.CommentStatus.COMMENT_EXIST;
import static com.e1i5.stackOverflow.comment.entity.Comment.CommentStatus.ORIGIN_COMMENT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommentServiceTest {
    @Mock
    CommentRepository commentRepository;

    @Mock
    QuestionRepository questionRepository;

    @Mock
    MemberService memberService;

    @Mock
    QuestionService questionService;

    @Mock
    CommentMapper mapper;

    @InjectMocks
    CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    @DisplayName("댓글조회 test")
    void refactorFindAll() {
        // given
        Long questionId = 1L;
        int page = 1;
        int size = 10;

        Pageable pageable = PageRequest.of(page-1, size);

        Comment comment = new Comment();  // 적절한 Comment 객체를 설정해야 할 수 있습니다.
        List<Comment> commentList = Collections.singletonList(comment);
        Page<Comment> commentPage = new PageImpl<>(commentList);

        given(commentRepository.findAllByQuestionQuestionId(questionId, pageable)).willReturn(commentPage);
        given(mapper.commentsToCommentResponseDtos(commentList)).willReturn(Collections.singletonList(new CommentDto.Response(1L,
                1L,1L,"title1","admini","this is content",false,5,1,COMMENT_EXIST)));

        // when & then
        assertThrows(NullPointerException.class, () -> {
            commentService.refactorFindAll(questionId, page, size);
        });


    }

    @Test
    void updateComment() {

    }

    @Test
    void createComment() {
        // given
        Comment commentToCreate = new Comment();
        commentToCreate.setCommentId(2L);
        commentToCreate.setContent("This is a new comment");
        commentToCreate.setCommentStatus(Comment.CommentStatus.ORIGIN_COMMENT);

        // when
        when(commentRepository.save(any(Comment.class))).thenReturn(commentToCreate);

        Comment createdComment = commentService.createComment(commentToCreate, 1L, 1L);

        // then
        assertNotNull(createdComment);
        assertEquals(commentToCreate.getContent(), createdComment.getContent());
        assertEquals(Comment.CommentStatus.ORIGIN_COMMENT, createdComment.getCommentStatus());
    }

    @Test
    void deleteComment() {
    }

    @Test
    void findVerifiedComment() {
    }
}