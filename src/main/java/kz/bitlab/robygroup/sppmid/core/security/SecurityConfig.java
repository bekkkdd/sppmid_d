package kz.bitlab.robygroup.sppmid.core.security;

import kz.bitlab.robygroup.sppmid.core.services.EDSService;
import kz.bitlab.robygroup.sppmid.core.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@Order(1)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Autowired
    private EDSService edsService;

    @Bean
    public RequestBodyReaderAuthenticationFilter authenticationFilter() throws Exception {
        RequestBodyReaderAuthenticationFilter authenticationFilter
                = new RequestBodyReaderAuthenticationFilter(edsService, userService);
//        authenticationFilter.setAuthenticationSuccessHandler(this::loginSuccessHandler);
//        authenticationFilter.setAuthenticationFailureHandler(this::loginFailureHandler);
        authenticationFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/auth/login", "POST"));
        authenticationFilter.setAuthenticationManager(authenticationManagerBean());
        return authenticationFilter;
    }

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        http
//                .csrf()
//                .disable()
                .authorizeRequests()
                .antMatchers("/css/**", "/js/**").permitAll()
//                .antMatchers("/api/**").permitAll()

                .antMatchers("/").permitAll();


//        http
//                .formLogin()
//                .loginProcessingUrl("/auth/login").permitAll()
//                .loginPage("/login").permitAll()
//                .usernameParameter("user_login")
//                .passwordParameter("user_password")
//                .successForwardUrl("/")
//                .defaultSuccessUrl("/")
//                .failureUrl("/login?error");

        http
                .logout().permitAll()
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/login")
                .permitAll();

        http.formLogin().loginPage("/login").permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
