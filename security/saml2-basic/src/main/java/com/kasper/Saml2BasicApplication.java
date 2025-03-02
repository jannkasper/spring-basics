package com.kasper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.RelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.authentication.OpenSaml4AuthenticationRequestResolver;
import org.springframework.security.saml2.provider.service.web.authentication.Saml2AuthenticationRequestResolver;

@SpringBootApplication
public class Saml2BasicApplication {

	public static void main(String[] args) {
		SpringApplication.run(Saml2BasicApplication.class, args);
	}

//	@Bean
//	Saml2AuthenticationRequestResolver authenticationRequestResolver(RelyingPartyRegistrationRepository registrations) {
//		RelyingPartyRegistrationResolver registrationResolver =
//				new DefaultRelyingPartyRegistrationResolver(registrations);
//		OpenSaml4AuthenticationRequestResolver authenticationRequestResolver =
//				new OpenSaml4AuthenticationRequestResolver(registrationResolver);
//		authenticationRequestResolver.setAuthnRequestCustomizer((context) -> context
//				.getAuthnRequest().setForceAuthn(true));
//		return authenticationRequestResolver;
//	}

}
