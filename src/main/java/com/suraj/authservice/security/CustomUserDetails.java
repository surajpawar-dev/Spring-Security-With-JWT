package com.suraj.authservice.security;

import com.suraj.authservice.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    // Return user ID for additional flexibility
    public UUID getUserId() {
        return user.getId();
    }

    // Return the user's email
    public String getEmail() {
        return user.getEmail();
    }

    // Return the user's phone number
    public String getPhoneNumber() {
        return user.getPhoneNumber();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert user role to a Spring Security GrantedAuthority
        return Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().name())
        );
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        // Implement account expiration logic if needed
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Implement account locking logic if needed
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Implement credential expiration logic if needed
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Implement user enabling/disabling logic if needed
        return true;
    }

    // Add this method to return the original user entity if needed
    public User getUser() {
        return user;
    }
}