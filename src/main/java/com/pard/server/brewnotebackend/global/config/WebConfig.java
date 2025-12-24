package com.pard.server.brewnotebackend.global.config;

import com.pard.server.brewnotebackend.domain.security.currentUser.CurrentUserArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    //정의한 custom method의 parameter를 controller에 내가 정의한 방식으로 자동 주입.
    /*
    JWT 필터에서 인증이 끝남
     -> Authentication 객체가 SecurityContext에 저장된다.
     ArgumentResolver은 그 SecurityContext에서 인증된 사용자를 꺼내
     컨트롤러 파라미터로 주입한다.

     Spring Security는 누가 요청했는지를 정하고 vs. Spring MVC는 해당 사람을 컨트롤러 파라미터로 어떻게 넘길지 정한다.

     참고로 우리가 사용하는 @RequestParam, @PathVariable, @RequestBody 모두 ArgumentResolver이다.
     그래서 우리는 @CurrentUser어노테이션 생성을 하여, 위 어노테이션과 같은 방식으로 파라미터의 값을 주입할 예정이다.

     하지만 @CurrnetUser이 정확히 뭔지 모르고, 어디서 Member을 가져와야 하는지도 모른다.

     그래서 방법!) 우리가 직접 Spring을 뜯어보고, 직접 등록한다!
     바로 HandlerMethodArgumentResolvers 과 addArgumentSolver를 사용해서!
     */
    private final CurrentUserArgumentResolver currentUserArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserArgumentResolver);
    }
}
