package com.e1i5.stackOverflow.comment.service;

import com.e1i5.stackOverflow.comment.dto.CommentDto;
import com.e1i5.stackOverflow.comment.entity.Comment;
import com.e1i5.stackOverflow.comment.mapper.CommentMapper;
import com.e1i5.stackOverflow.comment.repository.CommentRepository;
import com.e1i5.stackOverflow.dto.MultiResponseDto;
import com.e1i5.stackOverflow.exception.BusinessLogicException;
import com.e1i5.stackOverflow.exception.ExceptionCode;
import com.e1i5.stackOverflow.member.entity.Member;
import com.e1i5.stackOverflow.member.repository.MemberRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import static org.mockito.Mockito.*;

import static com.e1i5.stackOverflow.comment.entity.Comment.CommentStatus.COMMENT_EXIST;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommentServiceTest {
    @Mock
    CommentRepository commentRepository;

    @Mock
    QuestionRepository questionRepository;

    @Mock
    MemberRepository memberRepository;

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
    @DisplayName("댓글 수정 - 해당 댓글 작성자가 아닌경우 예외처리")
    void updateComment() {
        // given
        Long commentId = 10L;
        Long memberId = 1L;

        assertThrows(BusinessLogicException.class, () ->{
            commentService.VerifyCommentAuthor(commentId, memberId);
        });
    }

    @Test
    @DisplayName("댓글 등록 - 회원이 아닌 경우 댓글 등록 예외처리")
    void createComment_guest() {
        // given
        Long memberId = 0L; // guest인 경우 memberId는 0이라 가정

        when(memberService.findVerifiedMemberById(memberId))
                .thenThrow(new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

    }

    @Test
    @DisplayName("댓글 등록 - 질문이 존재하지 않으면 예외 처리")
    @Transactional
    void createComment_not_exist() {
        // given
        Long questionId = 1L;

        when(questionService.findVerifiedQuestion(questionId))
                .thenThrow(new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND));

    }

    @Test
    void deleteComment() {
    }

    @Test
    @DisplayName("작성자 확인 실패 테스트")
    void findVerifiedComment() {
    }
}