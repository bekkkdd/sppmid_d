package kz.bitlab.robygroup.sppmid.core.security;

import kz.bitlab.robygroup.sppmid.core.models.eds.EDSUser;
import kz.bitlab.robygroup.sppmid.core.services.EDSService;
import kz.bitlab.robygroup.sppmid.core.services.UserService;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.util.Objects;

public class RequestBodyReaderAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private EDSService edsService;
    private UserService userService;

    public RequestBodyReaderAuthenticationFilter(EDSService edsService, UserService userService) {
        this.edsService = edsService;
        this.userService = userService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        String userLogin = request.getParameter("user_login");
        String userPassword = request.getParameter("user_password");

        if (Objects.nonNull(userLogin) && Objects.nonNull(userPassword)) {

            UsernamePasswordAuthenticationToken token
                    = new UsernamePasswordAuthenticationToken(userLogin, userPassword);

            setDetails(request, token);
            return this.getAuthenticationManager().authenticate(token);

        } else {
            try {
                Part userEds = request.getPart("user_eds");
                EDSUser edsUserFromFile = edsService.getEdsUserFromFile(userEds.getInputStream(), userPassword);
                if (Objects.nonNull(edsUserFromFile)) {
                    String iin = edsUserFromFile.getSerialNumber();
                    if (iin.startsWith("IIN")) {
                        iin = iin.replaceAll("IIN", "");
                    }
                    UserDetails user = userService.loadUserByUsername(iin);
                    if (Objects.nonNull(user)) {
                        return new CustomAuthentication(user);
                    }else{
                        response.sendRedirect("/login?error");
                    }
                }
            } catch (Exception e) {
                throw new InternalAuthenticationServiceException("Test ", e);
            }
        }
        throw new InternalAuthenticationServiceException("Test ");
    }
}