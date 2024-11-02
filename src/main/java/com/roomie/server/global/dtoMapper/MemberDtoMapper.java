package com.roomie.server.global.dtoMapper;

import com.roomie.server.domain.member.domain.Member;
import com.roomie.server.domain.member.dto.response.MemberResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MemberDtoMapper {

    MemberDtoMapper INSTANCE = Mappers.getMapper(MemberDtoMapper.class);

    @Mapping(target = "roomieId", source = "member.roomie.id")
    MemberResponseDto toMemberResponseDto(Member member);

}
