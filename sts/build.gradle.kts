plugins {
    java
}

repositories {
    mavenCentral()
    maven("https://dev.webswing.org/public/nexus/repository/webswing-3rd-parties") {
        content {
            includeModule("javax.jnlp", "jnlp-api")
        }
    }
}

dependencies {
    implementation(project(":query"))
    implementation("javax.xml.bind", "jaxb-api", "2.3.1")
    implementation("javax.xml.ws", "jaxws-api", "2.3.1")
    implementation("javax.xml.soap", "javax.xml.soap-api", "1.4.0")
    implementation("javax.jws", "javax.jws-api", "1.1")
    implementation("com.formdev", "flatlaf", "3.0")
    implementation("javax.jnlp", "jnlp-api", "8.0")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "utf-8"
    }
}
