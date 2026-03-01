package com.hiccup.cura.security;

import com.hiccup.cura.enums.Role;
import com.hiccup.cura.model.User;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    @Getter
    @Setter
    private User user;
    
    @ElementCollection(fetch= FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles=new HashSet<>();

    public CustomUserDetails(User user){
        this.user=user;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(
                role-> new SimpleGrantedAuthority("ROLE_"+role.name())
        ).collect(Collectors.toSet());
    }

    public Set<Role> getRoles() {
        return user.getRoles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public @Nullable String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getEmail();
    }
}
