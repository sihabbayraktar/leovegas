package org.leovegas.wallet.service;

import org.leovegas.wallet.exception.AuthorizationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public void checkUserIsAccessingAnotherUserBalance(String userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"))){
            if(!authentication.getName().equals(userId)) {
                throw new AuthorizationException("Not allowed to see user balance");
            }
        }
    }

    public void checkUserIsMakingTransactionForAnotherUser(String userId) {
        if(!SecurityContextHolder.getContext().getAuthentication().getName().equals(userId)) {
            throw new AuthorizationException("Not allowed to make change");
        }
    }

    public void checkUserIsAccesingAnotherUserTransactionHistory(String userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"))){
            if(!authentication.getName().equals(userId)) {
                throw new AuthorizationException("Not allowed to see transaction history");
            }
        }
    }
}
