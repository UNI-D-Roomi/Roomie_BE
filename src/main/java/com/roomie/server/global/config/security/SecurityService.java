package com.roomie.server.global.config.security;

import com.roomie.server.domain.member.domain.Member;
import com.roomie.server.domain.member.domain.repository.MemberRepository;
import com.roomie.server.global.exceptions.ErrorCode;
import com.roomie.server.global.exceptions.InternalServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SecurityService {

    private final MemberRepository memberRepository;

    /*
    public boolean isNotTemporaryUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            String userLoginId = ((UserDetails) principal).getUsername();

            Member member = memberRepository.findByLoginId(userLoginId).orElseThrow(
                    () -> new InternalServerException(ErrorCode.INTERNAL_SERVER, "Token에 해당하는 사용자 정보를 찾을 수 없습니다. 관리자에게 문의 바랍니다."));

            return !user.getRoles().contains(UserStatus.TEMPORARY);
        }

        return false;
    }

     */

    public Member getUserByUserDetails(UserDetails userDetails) {
        return memberRepository.findByLoginId(userDetails.getUsername()).orElseThrow(
                () -> new InternalServerException(ErrorCode.INTERNAL_SERVER, "Token에 해당하는 사용자 정보를 찾을 수 없습니다. 관리자에게 문의 바랍니다."));
    }

}
