package com.e1i5.stackOverflow.questionVote.mapper;

import com.e1i5.stackOverflow.questionVote.dto.QuestionVoteDto;
import com.e1i5.stackOverflow.questionVote.entity.QuestionVote;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-11-26T22:38:53+0900",
    comments = "version: 1.5.1.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.5.1.jar, environment: Java 11.0.18 (Azul Systems, Inc.)"
)
@Component
public class QuestionVoteMapperImpl implements QuestionVoteMapper {

    @Override
    public QuestionVote QuestionVoteDtoToQuestionVote(QuestionVoteDto questionVoteDto) {
        if ( questionVoteDto == null ) {
            return null;
        }

        QuestionVote questionVote = new QuestionVote();

        return questionVote;
    }
}
