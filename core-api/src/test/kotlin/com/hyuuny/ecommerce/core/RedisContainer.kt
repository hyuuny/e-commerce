package com.hyuuny.ecommerce.core

import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

class RedisContainer(imageName: String) :
    GenericContainer<RedisContainer>(DockerImageName.parse(imageName))