package com.webwarriors.bb.configs;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import com.webwarriors.bb.services.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig{
	
	@Autowired 
	private UserService userDetailsService;
	
	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
	        .securityContext()
	        .securityContextRepository(new HttpSessionSecurityContextRepository())
	        .and()
            .csrf().disable()
            .authorizeRequests()
            .requestMatchers("/", "/index.html","/static/**", "/assets/**","/error","/api/auth","api/sessionDetails","api/authDetails","api/user/").permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .logout()  // This enables the default logout mechanism
	            .logoutUrl("/api/logout")  // Specifies the logout URL, defaults to "/logout" if not specified
	            .logoutSuccessUrl("/index.html")  // Redirect to the home page after logout
	            .invalidateHttpSession(true)  // Invalidates the HTTP session
	            .deleteCookies("JSESSIONID");  // Deletes the JSESSIONID cookie
            

        return http.build();
    }
    
	//only for test
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new PlainTextPasswordEncoder();
//    }

    @Bean
    public UsernamePasswordAuthenticationFilter authenticationFilter() throws Exception {
        UsernamePasswordAuthenticationFilter filter = new UsernamePasswordAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManagerBean());
        filter.setAuthenticationSuccessHandler(successHandler()); 
        return filter;
    }
    
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new SimpleUrlAuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        };
    }
    
//    @Bean
//    public UsernamePasswordAuthenticationFilter authenticationFilter() throws Exception {
//        UsernamePasswordAuthenticationFilter filter = new UsernamePasswordAuthenticationFilter();
//        filter.setAuthenticationManager(authenticationManagerBean());
//
//        // Setting custom success handler
//        filter.setAuthenticationSuccessHandler(new SimpleUrlAuthenticationSuccessHandler() {
//            @Override
//            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//                                                Authentication authentication) throws IOException, ServletException {
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//                clearAuthenticationAttributes(request);
//                response.getWriter().write("{\"status\":\"success\"}");
//                response.setStatus(HttpServletResponse.SC_OK);
//            }
//        });
//
//        // Setting custom failure handler
//        filter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler() {
//            @Override
//            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
//                                                AuthenticationException exception) throws IOException, ServletException {
//                saveException(request, exception);
//                response.getWriter().write("{\"status\":\"failure\",\"message\":\"" + exception.getMessage() + "\"}");
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            }
//        });
//
//        return filter;
//    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return new ProviderManager(Collections.singletonList(authenticationProvider()));
    }

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

}
