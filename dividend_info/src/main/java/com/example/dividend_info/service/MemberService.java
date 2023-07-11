package com.example.dividend_info.service;

import com.example.dividend_info.exception.impl.AlreadyExistUserException;
import com.example.dividend_info.exception.impl.IdNotExistsException;
import com.example.dividend_info.exception.impl.PasswordNotMatchedException;
import com.example.dividend_info.persist.entity.MemberEntity;
import com.example.dividend_info.persist.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.dividend_info.model.Auth;
@Slf4j
@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.memberRepository.findByUsername(username)
                .orElseThrow(() -> new IdNotExistsException());
    }

    public MemberEntity register(Auth.SignUp member){
        boolean exists = this.memberRepository.existsByUsername(member.getUsername());
        if(exists) {
            throw new AlreadyExistUserException;
        }

        member.setPassword(this.passwordEncoder.encode(member.getPassword()));

        return this.memberRepository.save(member.toEntity());

    }

    public MemberEntity authentication(Auth.SingIn member){
        var user = this.memberRepository.findByUsername(member.getUsername())
                .orElseThrow(() -> new IdNotExistsException());

        if(this.passwordEncoder.matches(member.getPassword(), user.getPassword())){
            throw new PasswordNotMatchedException();
        }
        return user;
    }
}
