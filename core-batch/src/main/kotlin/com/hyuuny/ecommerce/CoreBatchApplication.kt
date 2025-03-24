package com.hyuuny.ecommerce

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
class CoreBatchApplication

fun main(args: Array<String>) {
    runApplication<CoreBatchApplication>(*args)
}