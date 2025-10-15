package com.example.authsystem.security;

import com.example.authsystem.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

import static com.example.authsystem.enums.UserStatus.ACTIVE;
import static com.example.authsystem.enums.UserStatus.DELETED;


public record CustomUserDetails(UserEntity user) implements UserDetails {
    public Long getId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.getUserRole() == null)
            return Collections.emptyList();

        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getUserStatus() == null || !user.getUserStatus().equals(DELETED);
    }

    @Override
    public boolean isEnabled() {
        return user.getUserStatus() != null && user.getUserStatus().equals(ACTIVE);
    }
}