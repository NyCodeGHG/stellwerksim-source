plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("javax.xml.bind", "jaxb-api", "2.3.1")
    implementation("javax.xml.ws", "jaxws-api", "2.3.1")
    implementation("javax.jws", "javax.jws-api", "1.1")
    runtimeOnly("com.sun.xml.ws", "jaxws-rt", "2.3.1")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "utf-8"
    }
}
