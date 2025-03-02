# Spring Boot HTTPS Configuration Guide

This guide provides step-by-step instructions to configure HTTPS for a Spring Boot application using a self-signed certificate.

## Prerequisites

- Java 17 or later
- Maven
- Spring Boot 3.x

## Step 1: Create a Self-Signed Certificate

1. Open a terminal/command prompt
2. Navigate to your project's root directory
3. Run the following command to create a keystore with a self-signed certificate:

```bash
keytool -genkeypair -alias localhost -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore src/main/resources/keystore.p12 -validity 365
```

4. You will be prompted to:
   - Enter a keystore password (remember this for later configuration)
   - Provide information for the certificate (name, organization, etc.)
   - Confirm the information is correct

> **Note:** The password must be at least 6 characters long.

## Step 2: Configure Spring Boot for HTTPS

1. Open or create `src/main/resources/application.properties`
2. Add the following SSL configuration:

```properties
# SSL Configuration
server.port=8443
server.ssl.enabled=true
server.ssl.key-store=file:src/main/resources/keystore.p12
server.ssl.key-store-password=YOUR_PASSWORD
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=localhost
```

3. Replace `YOUR_PASSWORD` with the password you entered when creating the keystore

## Step 3: Build the Application

Build your Spring Boot application using Maven:

```bash
./mvnw clean package
```

## Step 4: Run the Application

Run your Spring Boot application:

```bash
java -jar target/ssl-basic-0.0.1-SNAPSHOT.jar
```

## Step 5: Access the Application

Open a web browser and navigate to:

```
https://localhost:8443
```

> **Important:** Since you're using a self-signed certificate, your browser will display a security warning. Click "Advanced" and then "Proceed to localhost (unsafe)" (or similar options depending on your browser) to access the application.

## Troubleshooting

### Error: "Unable to create key store: Could not load store from 'classpath:keystore.p12'"

**Solution:** Make sure the keystore file exists in the correct location and try using an absolute file path:
```properties
server.ssl.key-store=file:src/main/resources/keystore.p12
```

### Error: "Bad Request - This combination of host and port requires TLS"

**Solution:** Make sure you're accessing the application using the HTTPS protocol:
```
https://localhost:8443
```
Instead of:
```
http://localhost:8443
```

### Error: "The keystore password is incorrect"

**Solution:** Double-check that the password in `application.properties` matches the one you used when creating the keystore.

## Production Considerations

For production environments:
- Obtain a certificate from a trusted Certificate Authority instead of using a self-signed certificate
- Store sensitive information (like keystore passwords) as environment variables or in a secure configuration store
- Consider using a reverse proxy (like Nginx) to handle SSL termination

## Additional Resources

- [Spring Boot SSL Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#howto.webserver.configure-ssl)
- [Java Keytool Documentation](https://docs.oracle.com/en/java/javase/17/docs/specs/man/keytool.html) 