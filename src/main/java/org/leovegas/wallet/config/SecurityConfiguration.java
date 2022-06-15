package org.leovegas.wallet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
       auth.inMemoryAuthentication()
               .withUser("5fc03087-d265-11e7-b8c6-83e29cd24f4c").password("userpass").roles("USER")
               .and()
               .withUser("07344d08-ec0d-11ec-8ea0-0242ac120002").password("userpass").roles("USER")
               .and()
               .withUser("0ff4ca58-ec0d-11ec-8ea0-0242ac120002").password("userpass").roles("USER")
               .and()
               .withUser("1490427c-ec0d-11ec-8ea0-0242ac120002").password("userpass").roles("USER")
               .and()
               .withUser("ADMIN").password("adminpass").roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/payment/*").hasRole("USER")
                .antMatchers("/wallet/allbalance").hasRole("ADMIN")
                .antMatchers("/transaction/*", "/wallet/userbalance").hasAnyRole("ADMIN", "USER");

    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
