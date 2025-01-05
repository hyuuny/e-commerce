package com.hyuuny.ecommerce.core

import com.hyuuny.ecommerce.core.api.v1.config.TestContainerConfig
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest

@ExtendWith(TestContainerConfig::class)
@Retention(AnnotationRetention.RUNTIME)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
annotation class TestContainer
