package nextstep.auth.authentication;

import nextstep.auth.context.Authentication;
import nextstep.auth.context.SecurityContextHolder;
import nextstep.auth.token.JwtTokenProvider;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class BearerTokenAuthenticationFilter implements HandlerInterceptor {
    private JwtTokenProvider jwtTokenProvider;

    public BearerTokenAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            String requestToken = AuthorizationExtractor.extract(request, AuthorizationType.BEARER);

            if (jwtTokenProvider.validateToken(requestToken)) {
                String email = jwtTokenProvider.getPrincipal(requestToken);
                List<String> roles = jwtTokenProvider.getRoles(requestToken);

                Authentication authentication = new Authentication(email, roles);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            return true;
        } catch (Exception e) {
            return true;
        }
    }
}
