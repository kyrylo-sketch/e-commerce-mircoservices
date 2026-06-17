package com.wex.user_service.security;

import com.wex.user_service.model.User;
import com.wex.user_service.model.UserPrincipal;
import com.wex.user_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



@Slf4j
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Loading user details for user={}", email);
        User customer = repo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));

        if (customer == null) {
            log.warn("User details not found for email={}", email);
            throw new UsernameNotFoundException("User not found");
        }

        log.info("User details found for email={}", email);
        return new UserPrincipal(customer);
    }
}
