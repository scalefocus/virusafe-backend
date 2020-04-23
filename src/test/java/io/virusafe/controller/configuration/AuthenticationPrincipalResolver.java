package io.virusafe.controller.configuration;

import io.virusafe.security.principal.UserPrincipal;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class AuthenticationPrincipalResolver implements HandlerMethodArgumentResolver {

    private final String userGuid;

    private final String identificationNumber;

    public AuthenticationPrincipalResolver(final String userGuid, final String identificationNumber) {
        this.userGuid = userGuid;
        this. identificationNumber = identificationNumber;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(UserPrincipal.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return UserPrincipal.builder()
                .userGuid(userGuid)
                .identificationNumber(identificationNumber)
                .build();
    }
}
