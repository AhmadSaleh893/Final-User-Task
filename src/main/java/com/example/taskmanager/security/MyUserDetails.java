package com.example.taskmanager.security;


import com.example.taskmanager.repository.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class MyUserDetails implements UserDetails {
    private String name;
    private String username;
    private String password;
    private int age;
    private Boolean active;
    private List<GrantedAuthority> authorities;

    public MyUserDetails(User user) {
        if (user.getPassword() != null || user.getEmail() != null) {
            this.name = user.getName();
            this.username = user.getEmail();
            this.password = user.getPassword();
            this.age = user.getAge();
            this.active = active;
            this.authorities = Arrays.stream(user.getRoles().split(","))
                    .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        }
        else
        {
           new MyUserDetails();
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }


}
