package com.hyuuny.ecommerce.core.api.v1.config

import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

class TestContainerConfig : BeforeAllCallback {

    companion object {
        private const val REDIS_IMAGE = "redis:7.4.1-alpine"
        private const val REDIS_PORT = 6379
    }

    private lateinit var redis: GenericContainer<*>

    override fun beforeAll(context: ExtensionContext) {
        redis = GenericContainer(DockerImageName.parse(REDIS_IMAGE)).withExposedPorts(REDIS_PORT)
        redis.start()
        System.setProperty("spring.data.redis.host", redis.host)
        System.setProperty("spring.data.redis.port", redis.getMappedPort(REDIS_PORT).toString())
    }
}

