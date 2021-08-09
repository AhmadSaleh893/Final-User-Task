package com.example.taskmanager.security;

import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// there is a filter that does the following
// UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
//         userDetails, null, userDetails.getAuthorities());
//         usernamePasswordAuthenticationToken
//         .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//         SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
// here since we get a token from the user
// try to extract userinfo from that token if it succeeds then pass the request to next filter

// Authorization
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private MyUserDetailsService service;
    private JwtUtils utils;
    private final UserRepository userRepository;

    @Autowired
    public JwtRequestFilter(MyUserDetailsService service
            , JwtUtils utils
            , UserRepository userRepository) {
        this.service = service;
        this.utils = utils;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request
            , HttpServletResponse response
            , FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");
        String vvv = authorizationHeader;
        if (vvv != null) {
            TOKEN_GETTER.TOKEN = vvv.split(" ")[1];
        }
        String email = null;
        String token;

        // get the token
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.replace("Bearer ", "");
            // here if token is malformed an exception will occur
            email = utils.extractEmail(token);
        }

        // if getAuthentication=null -> this means that theres no security context until now
        if (userRepository.findUserByEmail(email).isPresent() /*email != null*/ && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.service.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            usernamePasswordAuthenticationToken
                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }

        filterChain.doFilter(request, response);
    }
}
