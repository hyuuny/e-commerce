allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("com.h2database:h2")
    implementation("com.querydsl:querydsl-jpa:${property("querydslVersion")}:jakarta")
    implementation("com.querydsl:querydsl-apt:${property("querydslVersion")}:jakarta")
    implementation("jakarta.persistence:jakarta.persistence-api")
    kapt("com.querydsl:querydsl-apt:${property("querydslVersion")}:jakarta")
}
