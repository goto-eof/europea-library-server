package com.andreidodu.europealibrary.annotation.security;

import com.andreidodu.europealibrary.constants.AuthConst;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyAuthority('" + AuthConst.AUTHORITY_SCOPE_USER + "','" + AuthConst.AUTHORITY_SCOPE_ADMINISTRATOR + "')")
public @interface AllowOnlyAuthenticatedUsers {
}
