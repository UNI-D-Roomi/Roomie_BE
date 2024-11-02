package com.roomie.server.global.config.security.userDetails;

import com.roomie.server.domain.member.domain.Member;
import com.roomie.server.domain.member.domain.repository.MemberRepository;
import com.roomie.server.global.exceptions.BadRequestException;
import com.roomie.server.global.exceptions.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String loginId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(member);
    }

    public UserDetails loadUserByUserId(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "User not found"));

        return new CustomUserDetails(member);
    }

}
