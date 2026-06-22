package security;



import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.prince.ems.exception.InvalidTokenException;
import com.prince.ems.exception.TokenExpiredException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component("util")
public class JwtUtil {
	
	@Value("${jwt.secret}")
	public String secret;
	
	@Value("${jwt.accessExpiration}")
	public long accessExpiration;
	
	@Value("${jwt.refreshExpiration}")
	public long refreshExpiration;
	
	public SecretKey SECRET;
	
	public void init() {
		this.SECRET = Keys.hmacShaKeyFor(secret.getBytes());
	}
	
	public String generateToken(String username) {
		
		return Jwts.builder()
				.claim(Claims.SUBJECT, username)
				.claim("type", "access")
				.claim(Claims.ISSUED_AT, new Date())
				.claim(Claims.EXPIRATION, new Date(
						System.currentTimeMillis() + accessExpiration))
				.signWith(SECRET)
				.compact();
		
	}
	
	public String refreshGenerateTOken(String username) {
		
		return Jwts.builder()
				.claim(Claims.SUBJECT, username)
				.claim("type", "refresh")
				.claim(Claims.ISSUED_AT, new Date())
				.claim(Claims.EXPIRATION, new Date(
						System.currentTimeMillis() + refreshExpiration))
				.signWith(SECRET)
				.compact();
	}
	
	public boolean validateToken(String token) {
		
		try {
			Jwts.parser()
				.verifyWith(SECRET)
				.build()
				.parseSignedClaims(token);
				return true;
		} catch (ExpiredJwtException e) {
			throw new TokenExpiredException("Token Expired");
		} catch (JwtException | IllegalArgumentException e) {
			throw new InvalidTokenException("Invalid Token");
		}
		
	}

}
