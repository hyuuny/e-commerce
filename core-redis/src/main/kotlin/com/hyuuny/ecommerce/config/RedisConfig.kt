package com.hyuuny.ecommerce.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.spring.data.connection.RedissonConnectionFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.cache.RedisCacheWriter
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
@EnableCaching
class RedisConfig(
    @Value("\${spring.data.redis.host}") private val redisHost: String,
    @Value("\${spring.data.redis.port}") private val redisPort: Int,
    @Value("\${spring.cache.redis.time-to-live}") private val ttl: Long,
    @Value("\${spring.data.redisson.connection.pool-size}") private val poolSize: Int,
    @Value("\${spring.data.redisson.connection.minimum-idle-size}") private val minIdleSize: Int
) {

    @Bean
    fun redissonClient(): RedissonClient {
        val config = org.redisson.config.Config().apply {
            useSingleServer().apply {
                address = "redis://$redisHost:$redisPort"
                connectionPoolSize = poolSize
                connectionMinimumIdleSize = minIdleSize
            }
        }
        return Redisson.create(config)
    }

    @Bean
    fun redissonConnectionFactory(redissonClient: RedissonClient): RedissonConnectionFactory =
        RedissonConnectionFactory(redissonClient)

    @Bean
    fun redisTemplate(connectionFactory: RedissonConnectionFactory): RedisTemplate<String, Any> =
        RedisTemplate<String, Any>().apply {
            this.connectionFactory = connectionFactory
            keySerializer = StringRedisSerializer()
            valueSerializer = GenericJackson2JsonRedisSerializer()
        }

    @Bean
    fun cacheManager(connectionFactory: RedissonConnectionFactory): CacheManager {
        val objectMapper = ObjectMapper().apply {
            registerKotlinModule()
            registerModule(JavaTimeModule())
            activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder().allowIfBaseType(Any::class.java).build(),
                ObjectMapper.DefaultTyping.EVERYTHING
            )
        }

        val cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(ttl))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    GenericJackson2JsonRedisSerializer(objectMapper)
                )
            )

        return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory))
            .cacheDefaults(cacheConfig)
            .build()
    }
}
