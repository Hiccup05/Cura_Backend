package com.hiccup.cura.security;

import com.hiccup.cura.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class CustomUser implements UserDetails {
    private User user;

    public CustomUser(User user){
        this.user=user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return user.getRole().stream().map(
                role->new SimpleGrantedAuthority("ROLE_"+role.getName().name())
        ).collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    public Long getId(){
        return user.getId();
    }
}
