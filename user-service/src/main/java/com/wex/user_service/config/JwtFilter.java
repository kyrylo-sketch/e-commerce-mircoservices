package com.wex.user_service.config;

import com.wex.user_service.security.JWTService;
import com.wex.user_service.security.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
//OncePerRequestFilter - filter ktory gwarantuje ze doFilterInternal wykona sie dokladnie per request
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
    ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJuYXZpbiIsImlhdCI6MTc3Mjk3MDAxOSwiZXhwIjoxNzcyOTcwMTI3fQ.-xlBbM3DoKs4v7PoQga5B2LHpuC_zWosGBqZ-xIM0zw
        //czytanie naglowka
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if(authHeader != null && authHeader.startsWith("Bearer ")){

            token = authHeader.substring(7);

            try {
                //wyciaganie username
                username = jwtService.extractUserName(token);
            } catch (Exception e) {
                //jesli token jest nie poprawny to
                //rzuca wyjatek i przrkazujemu dalej bez ustawiania uzytkownika
                filterChain.doFilter(request, response);
                return;
            }
        }

        //pierwszy warunek jest ze token byl obecny i dalo sie go odcyztac
        //drugi warunek jest ze uzytkownik nie jest jeszcze ustawiony w tym zadaniu
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            //uzywamy getBean bo jwtfilter jest zawczesnie
            UserDetails userDetails = context.getBean(MyUserDetailsService.class).loadUserByUsername(username);

            //walidacja sprawdza czy username z tokunu zgadza sie z tym z bazy i czy token nie wygasl
            if(jwtService.validateToken(token, userDetails)){
                //ustawianie SecurityContext
                //UsernamePasswordAuthenticationToken - reprezentuje kto jest zalogowany
                //userDetails - obiekt uzytkownika
                //null - haslo
                //userDetails.getAuthorities - role/uprawnienia uzytkownika
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                //dodaje metadane requestu
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //w ten moment wiemy ze uzytkownik jest zalogowany
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        //przekazuje zadanie do nastepnego filtra
        filterChain.doFilter(request, response);


    }
}


