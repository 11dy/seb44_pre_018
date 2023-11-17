package com.e1i5.stackOverflow.comment.service;

import com.e1i5.stackOverflow.comment.entity.Comment;
import com.e1i5.stackOverflow.comment.mapper.CommentMapper;
import com.e1i5.stackOverflow.dto.MultiResponseDto;
import com.e1i5.stackOverflow.exception.BusinessLogicException;
import com.e1i5.stackOverflow.exception.ExceptionCode;
import com.e1i5.stackOverflow.comment.repository.CommentRepository;
import com.e1i5.stackOverflow.member.entity.Member;
import com.e1i5.stackOverflow.member.service.MemberService;
import com.e1i5.stackOverflow.question.entity.Question;
import com.e1i5.stackOverflow.question.repository.QuestionRepository;
import com.e1i5.stackOverflow.question.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final QuestionRepository questionRepository;
    private final MemberService memberService;
    private final QuestionService questionService;
    private final CommentMapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    public CommentService(CommentRepository commentRepository,
                          QuestionService questionService,
                          MemberService memberService,
                          QuestionRepository questionRepository,
                          CommentMapper mapper){
        this.questionService = questionService;
        this.memberService = memberService;
        this.commentRepository = commentRepository;
        this.questionRepository = questionRepository;
        this.mapper = mapper;
    }

    //댓글 조회 refactor
    @Transactional(readOnly= true)
    public Object refactorFindAll(Long questionId, int page, int size) {

//        Pageable pageable = PageRequest.of(page-1, size);
//
//        Page<Comment> comments = commentRepository.findAllByQuestionQuestionId(questionId, pageable);
//        List<Comment> commentList = comments.getContent();
//
//        return new MultiResponseDto<>(
//                mapper.commentsToCommentResponseDtos(commentList),
//                comments);
        try {
            Pageable pageable = PageRequest.of(page - 1, size);

            Page<Comment> comments = commentRepository.findAllByQuestionQuestionId(questionId, pageable);

            if (comments == null || comments.getContent() == null) {
                throw new NullPointerException("Comments or content is null");
            }

            List<Comment> commentList = comments.getContent();

            return new MultiResponseDto<>(
                    mapper.commentsToCommentResponseDtos(commentList),
                    comments);
        } catch (Exception e) {
            logger.info("예외발생");
            throw new NullPointerException("An exception occurred");
        }
    }



    // 댓글 수정 - 해당 댓글 작성자만 수정 가능 엔드포인트에서 멤버id, userid를 전달받음
    public Comment updateComment(Comment comment, Long commentId, Long memberId){
        VerifyCommentAuthor(commentId, memberId);

        Comment findComment = findVerifiedComment(comment.getCommentId());
        Optional.ofNullable(comment.getContent())
                .ifPresent(commentContent -> findComment.setContent(commentContent));
        Optional.ofNullable(comment.getModifiedAt())
                .ifPresent(commentModifiedAt ->findComment.setModifiedAt(commentModifiedAt));
        Optional.ofNullable(comment.getCommentStatus2())
                .ifPresent(commentStatus2 -> findComment.setCommentStatus(commentStatus2.MODIFIED_COMMENT));
        Comment modifiedComment = commentRepository.save(findComment);

        return modifiedComment;
    }

    // 댓글 생성 - 회원만 생성 가능
    public Comment createComment(Comment comment,  Long questionId, Long memberId){
        // 회원인지 파악
        Member findmember = memberService.findVerifiedMemberById(memberId);
        comment.setMember(findmember);

        //존재 질문인지 파악
        Question findQuestion = questionService.findVerifiedQuestion(questionId);
        comment.setQuestion(findQuestion);

        return commentRepository.save(comment);
    }


    //댓글 삭제 - 해당 댓글 작성자, 질문글 작성자 모두 삭제 가능.
    public void deleteComment(Long commentId, Long memberId) {
        Comment comment = findVerifiedComment(commentId);
        Long commentAuthorId = comment.getMember().getMemberId();
        Long questionAuthorId = comment.getQuestion().getQuestionId();

        if(memberId.equals(commentAuthorId) || memberId.equals(questionAuthorId)){
            Comment result = findVerifiedComment(commentId);
            commentRepository.delete(result);
        }else {
            throw new RuntimeException("권한이 없습니다.");
        }
    }

    // commentId에 해당하는 댓글을 찾는 메서드. 댓글이 존재하는지 확인한다.
    @Transactional
    public Comment findVerifiedComment(Long commentId){
        Optional<Comment> optionalComment =
                commentRepository.findById(commentId);
        Comment findComment =
                optionalComment.orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
        return findComment;
    }


    // 댓글 like count - 로그인 된 회원만 가능
    public void likeCount(Long commentId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
        comment.setLikeCount(comment.getLikeCount() + 1);
        commentRepository.save(comment);

    }

    // 댓글 dislike count - 로그인 된 회원만 가능
    public void dislikeCount(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
        comment.setDislikeCount(comment.getDislikeCount() + 1);
        commentRepository.save(comment);
    }

    // 채택 메서드
    public void chooseComment(Long commentId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
        if(comment.isChoose()){
            comment.setChoose(false);
        }else {
            comment.setChoose(true);
            comment.setCommentStatus(Comment.CommentStatus.ANSWER_COMMENT); }
        commentRepository.save(comment);

    }

    //질문 작성자인지 판단하는 메서드
    public void VerifyQuestionAuthor(Long commentId, Long memberId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
        Question question = comment.getQuestion();
        if(question == null || question.getMember() == null || question.getMember().getMemberId() != memberId){
            throw new BusinessLogicException(ExceptionCode.QUESTION_MEMBER_NOT_MATCH);
        }
    }


    //    전달 받은 memberId가 comment에 저장된 memberId와 같은지 비교
    public void VerifyCommentAuthor(Long commentId, Long memberId){
        Comment patchComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));

        if(patchComment.getMember() == null || patchComment.getMember().getMemberId() != memberId){
            throw new BusinessLogicException(ExceptionCode.NOT_A_COMMENT_WRITER);

        }

    }

}
