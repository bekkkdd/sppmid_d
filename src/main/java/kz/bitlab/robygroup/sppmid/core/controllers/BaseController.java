package kz.bitlab.robygroup.sppmid.core.controllers;

import kz.bitlab.robygroup.sppmid.core.services.UserService;
import kz.bitlab.robygroup.sppmid.core.models.user.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class BaseController {

    @Autowired
    protected UserService userService;

    public Users getUserData(){
        Users userData = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            User secUser = (User)authentication.getPrincipal();
            userData = userService.findByLogin(secUser.getUsername());
        }
        return userData;
    }

}
