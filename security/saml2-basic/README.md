# SAML2 Authentication with Spring Boot and KeyCloak

This repository provides a complete guide for setting up SAML2 authentication between a Spring Boot application and KeyCloak running on localhost with SSL.

## Overview

This guide covers:
1. Setting up SSL certificates for KeyCloak
2. Creating SAML2 certificates for your Spring Boot application
3. Configuring and running KeyCloak with Docker
4. Setting up SAML2 authentication in KeyCloak
5. Configuring and running your Spring Boot application

## Prerequisites

- Java 11 or higher
- Docker and Docker Compose
- OpenSSL
- Maven or Gradle

## Setup Guide

### 1. SSL Certificates for KeyCloak

#### Certificate Files
The following SSL certificate files are used for KeyCloak:

- `localhost-crt.pem` - The SSL certificate for localhost
- `localhost.key.pem` - The private key for the SSL certificate
- `truststore.jks` - Java KeyStore truststore containing the SSL certificate

#### Generating SSL Certificates
If the certificates are not already available, create them with:

```bash
# Create SSL certificate and private key
openssl req -x509 -newkey rsa:4096 -keyout localhost.key.pem -out localhost-crt.pem -days 365 -nodes \
  -subj "/CN=localhost" -addext "subjectAltName=DNS:localhost,IP:127.0.0.1"

# Create Java truststore
keytool -import -file localhost-crt.pem -alias localhost -keystore truststore.jks \
  -storepass 123456 -noprompt
```

This creates:
- A self-signed X.509 certificate valid for 365 days
- A 4096-bit RSA private key with no password protection
- A Java KeyStore truststore with password "123456"

### 2. SAML2 Certificates for Spring Boot

#### Certificate Files
For SAML2 authentication, the Spring Boot application requires:

- `rp-key.key` - The private key for the Relying Party
- `rp-certificate.crt` - The certificate for the Relying Party

#### Generating SAML2 Certificates
Generate these certificates with:

```bash
# Generate a keystore
keytool -genkeypair -alias rp -keyalg RSA -keysize 2048 -validity 3650 \
  -keystore rp-keystore.jks -storepass password -keypass password \
  -dname "CN=Relying Party, OU=Spring, O=Example, L=City, ST=State, C=US"

# Export the certificate
keytool -export -alias rp -file rp-certificate.crt -keystore rp-keystore.jks \
  -storepass password

# Export the private key
keytool -importkeystore -srckeystore rp-keystore.jks -srcalias rp \
  -destkeystore temp.p12 -deststoretype PKCS12 \
  -srcstorepass password -deststorepass password
openssl pkcs12 -in temp.p12 -nocerts -nodes -out rp-key.key -passin pass:password

# Clean up
rm temp.p12
```

Place the generated `rp-key.key` and `rp-certificate.crt` in the `src/main/resources` directory of your Spring Boot application.

### 3. Running KeyCloak

KeyCloak is configured to run with Docker Compose:

```bash
# Start KeyCloak
docker-compose up -d
```

Once started, KeyCloak is accessible at https://localhost:8443/auth/

> **Note:** When accessing KeyCloak for the first time, your browser may warn about an untrusted certificate. This is expected with self-signed certificates.

### 4. Configuring KeyCloak for SAML2


#### Create realm

1. Create a new realm:
   - Click "Add realm" in the upper left corner
   - Copy and paste `config/realm-export.json`
   - Save

#### Creating a User

1. Access the KeyCloak admin console at https://localhost:8443/auth/admin/
2. Log in with the admin credentials (default: username `admin`, password `admin`)
3. Create a user:
   - In your realm, go to "Users" → "Add user"
   - Fill in the required information (at minimum, the username)
   - Click "Save"
4. Set the user's password:
   - Go to the "Credentials" tab
   - Enter a password and confirm it
   - Set "Temporary" to "OFF"
   - Click "Set Password"

#### Obtaining the IdP Metadata

1. Go to the "Installation" tab of your SAML client
2. Select "SAML Metadata IDPSSODescriptor" from the dropdown
3. Download the XML file
4. Save it as `metadata-idp.xml` in your Spring application's `src/main/resources` directory

### 5. Configuring and Running the Spring Boot Application

1. Configure your Spring Boot application to use SAML2 authentication by adding the appropriate settings to your `application.yml` or `application.properties`:

```yaml
spring:
  security:
    saml2:
      relyingparty:
        registration:
          keycloak:
            signing:
              credentials:
                - private-key-location: classpath:rp-key.key
                  certificate-location: classpath:rp-certificate.crt
            identityprovider:
              metadata-uri: classpath:metadata-idp.xml
```

2. Start your Spring Boot application:

```bash
./mvnw spring-boot:run
```

3. Access your application at http://localhost:8080
   - You should be redirected to KeyCloak for authentication
   - Log in with the user you created
   - After successful authentication, you'll be redirected back to your application

## Security Considerations

### Development vs. Production
- These certificates are self-signed and intended for development only
- For production, obtain certificates from a trusted Certificate Authority
- Keep private keys secure and never commit them to version control
- Consider using a hardware security module for key storage in production

### Certificate Renewal
- SSL certificates expire after 365 days
- SAML2 certificates expire after 3650 days (10 years)
- Renew by running the generation commands again

## Troubleshooting

### Common Issues
- Browser certificate warnings are normal with self-signed certificates
- Ensure all paths in configuration files are correct
- Check KeyCloak logs if authentication fails
- Verify that the SAML client settings match your Spring application configuration

## References

- [Spring Security SAML2 Documentation](https://docs.spring.io/spring-security/reference/servlet/saml2/index.html)
- [KeyCloak Documentation](https://www.keycloak.org/documentation) 