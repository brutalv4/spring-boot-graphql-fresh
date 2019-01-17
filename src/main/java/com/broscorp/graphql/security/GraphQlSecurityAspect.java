package com.broscorp.graphql.security;

import lombok.extern.java.Log;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Log
public class GraphQlSecurityAspect {

  @Before("isDefinedInApplication() && isTargetAnnotatedWithGraphQLStuff() && !isUnsecuredMethod()")
  public void doAuthenticationCheck() {
    log.info("Executing security check on /graphql");
    if (!isUserAuthenticated()) {
      throw new AccessDeniedException("User not authenticated");
    }
  }

  @Pointcut("within(com.broscorp.graphql..*)")
  private void isDefinedInApplication() {
  }

  @Pointcut("within(@io.leangen.graphql.spqr.spring.annotation.GraphQLApi *)")
  private void isTargetAnnotatedWithGraphQLStuff() {
  }

  @Pointcut("@annotation(com.broscorp.graphql.security.annotation.Unsecured)")
  private void isUnsecuredMethod() {
  }

  private boolean isUserAuthenticated() {
    return SecurityContextHolder.getContext() != null &&
        SecurityContextHolder.getContext().getAuthentication() != null &&
        SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
        !AnonymousAuthenticationToken.class
            .isAssignableFrom(SecurityContextHolder.getContext().getAuthentication().getClass());
  }
}
