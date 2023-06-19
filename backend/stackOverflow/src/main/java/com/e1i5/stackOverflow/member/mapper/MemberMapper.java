package com.e1i5.stackOverflow.member.mapper;

import com.e1i5.stackOverflow.member.dto.MemberDto;
import com.e1i5.stackOverflow.member.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberMapper {
    Member memberSignupPostDtoToMember(MemberDto.SignupPost requestBody);
    Member memberLoginPostDtoToMember(MemberDto.LoginPost requestBody);

    Member memberPatchDtoToMember(MemberDto.Patch requestBody);
    MemberDto.NomalResponse memberToMemberResponseDto(Member member);
    List<MemberDto.NomalResponse> membersToMemberResponseDtos(List<Member> members);
}
