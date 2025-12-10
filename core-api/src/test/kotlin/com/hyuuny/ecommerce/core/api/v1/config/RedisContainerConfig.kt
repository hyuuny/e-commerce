package com.hyuuny.ecommerce.core.api.v1.config

import com.hyuuny.ecommerce.core.RedisContainer
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
abstract class RedisContainerConfig {

    companion object {
        val redis = RedisContainer("redis:7.2.4")
            .withExposedPorts(6379)
            .waitingFor(Wait.forListeningPort())
            .withReuse(true)
            .apply { start() }

        init {
            Runtime.getRuntime().addShutdownHook(Thread {
                println("Shutting down Redis container...")
                redis.stop()
            })
        }

        @JvmStatic
        @DynamicPropertySource
        fun redisProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.redis.host") { redis.host }
            registry.add("spring.data.redis.port") { redis.firstMappedPort }
        }
    }
}
