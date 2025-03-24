plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "e-commerce"
include(
    "core-api",
    "core-batch",
    "core-redis",
    "storage:db-core",
    "support:logging",
)
