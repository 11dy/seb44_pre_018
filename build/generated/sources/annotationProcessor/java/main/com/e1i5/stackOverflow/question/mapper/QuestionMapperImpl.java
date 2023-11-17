package com.e1i5.stackOverflow.question.mapper;

import com.e1i5.stackOverflow.comment.entity.Comment;
import com.e1i5.stackOverflow.member.entity.Member;
import com.e1i5.stackOverflow.question.dto.QuestionCommentDto;
import com.e1i5.stackOverflow.question.dto.QuestionDto;
import com.e1i5.stackOverflow.question.entity.Question;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-11-18T01:52:19+0900",
    comments = "version: 1.5.1.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.5.1.jar, environment: Java 11.0.18 (Azul Systems, Inc.)"
)
@Component
public class QuestionMapperImpl implements QuestionMapper {

    @Override
    public Question questionPostDtoToQuestion(QuestionDto.QuestionPostDto requestBody) {
        if ( requestBody == null ) {
            return null;
        }

        Question question = new Question();

        question.setTitle( requestBody.getTitle() );
        question.setContent( requestBody.getContent() );

        return question;
    }

    @Override
    public Question questionPatchDtoToQuestion(QuestionDto.QuestionPatchDto requestBody) {
        if ( requestBody == null ) {
            return null;
        }

        Question question = new Question();

        question.setQuestionId( requestBody.getQuestionId() );
        question.setTitle( requestBody.getTitle() );
        question.setContent( requestBody.getContent() );

        return question;
    }

    @Override
    public QuestionCommentDto commentToQuestionCommentDto(Comment comment) {
        if ( comment == null ) {
            return null;
        }

        String authenticatedMemberName = null;
        String imageName = null;
        String imagePath = null;
        int commentLikeCount = 0;
        int commentDislikeCount = 0;
        long commentId = 0L;
        String content = null;
        boolean choose = false;
        LocalDateTime createdAt = null;

        authenticatedMemberName = commentMemberName( comment );
        imageName = commentMemberProfileImageName( comment );
        imagePath = commentMemberProfileImagePath( comment );
        commentLikeCount = comment.getLikeCount();
        commentDislikeCount = comment.getDislikeCount();
        if ( comment.getCommentId() != null ) {
            commentId = comment.getCommentId();
        }
        content = comment.getContent();
        choose = comment.isChoose();
        createdAt = comment.getCreatedAt();

        QuestionCommentDto questionCommentDto = new QuestionCommentDto( commentId, authenticatedMemberName, content, imageName, imagePath, choose, commentLikeCount, commentDislikeCount, createdAt );

        return questionCommentDto;
    }

    private String commentMemberName(Comment comment) {
        if ( comment == null ) {
            return null;
        }
        Member member = comment.getMember();
        if ( member == null ) {
            return null;
        }
        String name = member.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String commentMemberProfileImageName(Comment comment) {
        if ( comment == null ) {
            return null;
        }
        Member member = comment.getMember();
        if ( member == null ) {
            return null;
        }
        String profileImageName = member.getProfileImageName();
        if ( profileImageName == null ) {
            return null;
        }
        return profileImageName;
    }

    private String commentMemberProfileImagePath(Comment comment) {
        if ( comment == null ) {
            return null;
        }
        Member member = comment.getMember();
        if ( member == null ) {
            return null;
        }
        String profileImagePath = member.getProfileImagePath();
        if ( profileImagePath == null ) {
            return null;
        }
        return profileImagePath;
    }
}
