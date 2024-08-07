package com.bigtree.auth.security;


import com.bigtree.auth.entity.User;
import com.bigtree.auth.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class AuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("#{'${auth.permitAll}'.split(',')}")
    List<String> permitAll;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String servletPath = request.getServletPath();
        log.info("Authorizing the request {}", servletPath);
        boolean whitelisted = false;
        for (String url : permitAll) {
            if ( servletPath.equalsIgnoreCase(url) || servletPath.startsWith(url)){
                whitelisted = true;
                break;
            }
        }

        if ( whitelisted){
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    "PermitAll", null, null);
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            log.info("Authorised");
        }else{
            log.info("The requested url is not whitelisted..");
            authorise(request);
        }


        chain.doFilter(request, response);
    }

    private void authorise(HttpServletRequest request){
        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String subjectType = null;
        String jwtToken = null;
        // JWT Token is in the form "Bearer token". Remove Bearer word and get
        // only the Token
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getSubject(jwtToken);
                subjectType = jwtTokenUtil.getSubjectType(jwtToken);
                log.info("Subject {}, SubjectType {}", username, subjectType);
            } catch (IllegalArgumentException e) {
                log.error("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                log.error("JWT Token has expired");
            }
        } else {
            log.error("JWT Token does not begin with Bearer String");
        }

        // Once we get the token validate it.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            if ( subjectType!= null && subjectType.equalsIgnoreCase("CustomerApp")){
                log.info("Verifying machine user access token");
                if (!jwtTokenUtil.isTokenExpired(jwtToken)){
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            username, null, null);
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // After setting the Authentication in the context, we specify
                    // that the current user is authenticated. So it passes the
                    // Spring Security Configurations successfully.
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    log.info("Authorised");
                }
            }else{
                log.info("Verifying customer access token");
                User customer = this.userRepository.findByEmail(username);
                if ( customer != null){
                    if (!jwtTokenUtil.isTokenExpired(jwtToken)){
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                customer, null, null);
                        usernamePasswordAuthenticationToken
                                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        // After setting the Authentication in the context, we specify
                        // that the current user is authenticated. So it passes the
                        // Spring Security Configurations successfully.
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                        log.info("Authorised");
                    }else{
                        log.error("Token expired");
                    }
                }else{
                    log.info("Not Authorised. Customer {} not found", username);
                }
            }

        }
    }
}