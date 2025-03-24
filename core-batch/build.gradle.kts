tasks.getByName("bootJar") {
    enabled = true
}

tasks.getByName("jar") {
    enabled = false
}

dependencies {
    implementation(project(":support:logging"))
    implementation(project(":storage:db-core"))
    implementation(project(":core-redis"))

    implementation("org.springframework.boot:spring-boot-starter-web")
}
