/**
 * 
 */
package com.example.accounts.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * @author User
 *
 */
public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>>{

	@Override
	public Collection<GrantedAuthority> convert(Jwt source) {
		
		Map<String, Object> realm_access = (Map<String, Object>) source.getClaims().get("realm_access");
		if(realm_access == null || realm_access.isEmpty()) {
			return new ArrayList<>();
		}
		
		Collection<GrantedAuthority> roles = ((List<String>)realm_access.get("roles")).stream().map( roleName -> "ROLE_"+roleName).map(SimpleGrantedAuthority::new)
		.collect(Collectors.toList());
		return roles;
	}

}
