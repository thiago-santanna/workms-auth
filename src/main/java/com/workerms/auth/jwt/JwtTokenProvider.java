package com.workerms.auth.jwt;

import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtTokenProvider {

	@Value("${secutity.jwt.secret-key.}")
	private String secretKey;

	@Value("${secutity.jwt.expire-in}")
	private long expireIn;

	@Qualifier("userService")
	@Autowired
	private UserDetailsService userDetailsService;

	@PostConstruct
	protected void init() {
		this.secretKey = Base64.getEncoder().encodeToString(this.secretKey.getBytes());
	}

	@SuppressWarnings("deprecation")
	public String createToken(String userName, List<String> roles) {
		Claims claims = Jwts.claims().setSubject(userName);
		claims.put("roles", roles);

		Date now = new Date();
		Date valid = new Date(now.getDate() + expireIn);

		String token = Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(valid)
				.signWith(SignatureAlgorithm.HS256, this.secretKey).compact();

		return token;
	}

	public Authentication getAuthentication(String token) {
		UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUserName(token));
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}
	
	public String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}
		
		return null;
	}

	private String getUserName(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
	}
	
	public boolean validateToken(String token) {
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			if(claims.getBody().getExpiration().before(new Date())) {
				return false;
			}
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

}
