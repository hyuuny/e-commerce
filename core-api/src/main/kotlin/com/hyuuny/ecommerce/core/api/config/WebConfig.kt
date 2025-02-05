package com.hyuuny.ecommerce.core.api.config

import com.hyuuny.ecommerce.core.support.AuthUserIdArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val authUserIdArgumentResolver: AuthUserIdArgumentResolver,
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(authUserIdArgumentResolver)
    }
}
