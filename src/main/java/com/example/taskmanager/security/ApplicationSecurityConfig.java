package com.example.taskmanager.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.example.taskmanager.security.ApplicationUserRole.USER;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {


    private final UserDetailsService userDetailsService;
    private JwtRequestFilter tokenFilter;

    @Autowired
    public ApplicationSecurityConfig(@Qualifier("myUserDetailsService") UserDetailsService userDetailsService
            , JwtRequestFilter tokenFilter) {
        this.userDetailsService = userDetailsService;
        this.tokenFilter = tokenFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
                .csrf().disable()

                .authorizeRequests()
                //.antMatchers("/").permitAll()

                .antMatchers(HttpMethod.POST, "/login")
                .permitAll()

                .antMatchers(HttpMethod.POST, "/signup")
                .permitAll()

                .antMatchers(HttpMethod.GET, "/user")
                //.hasAuthority(USER_READ.getPermission())
                .hasRole(USER.name())

                .antMatchers(HttpMethod.POST, "/user")
                //.hasAuthority(USER_WRITE.getPermission())
                .hasRole(USER.name())

                .antMatchers(HttpMethod.DELETE, "/user/**")
                // .hasAuthority(USER_DELETE.getPermission())
                .hasRole(USER.name())

                .antMatchers(HttpMethod.PUT, "/user/**")
                // .hasAuthority(USER_EDIT.getPermission())
                .hasRole(USER.name())
///////**************
                .antMatchers(HttpMethod.GET, "/task")
                //.hasAuthority(USER_READ.getPermission())
                .hasRole(USER.name())

                .antMatchers(HttpMethod.POST, "/task")
                //.hasAuthority(USER_WRITE.getPermission())
                .hasRole(USER.name())

                .antMatchers(HttpMethod.DELETE, "/task/**")
                // .hasAuthority(USER_DELETE.getPermission())
                .hasRole(USER.name())

                .antMatchers(HttpMethod.PUT, "/task/**")
                // .hasAuthority(USER_EDIT.getPermission())
                .hasRole(USER.name())
                .antMatchers(HttpMethod.GET, "/user")
                .permitAll()

                .antMatchers("/**").denyAll()
                .anyRequest().authenticated().and().
                exceptionHandling().and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        ;
        http.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);


    }

  /*  @Bean
    @Override
    protected UserDetailsService userDetailsService(){
        UserDetails annaSmithUser = User.builder()
                .username("annasmith")
                .password(passwordEncoder.encode("password"))
                //.roles(USER.name()) // ROLE_STUDENT
                .authorities(/*USER_EDIT.getPermission(),USER_READ.getPermission(),USER_WRITE.getPermission()USER.getGrantedAuthorities())
                .build();

        System.out.println(USER.getGrantedAuthorities());
        return new InMemoryUserDetailsManager(annaSmithUser);
    }*/

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // make authentication based on jpa
        // here authentication will be based on userDetailsService
        // it will try loading a user based on the implementation
        // here its implemented to load a user using jpa
        auth.userDetailsService(userDetailsService);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


}

