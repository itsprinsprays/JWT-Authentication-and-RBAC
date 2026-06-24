package security1;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component("util")
public class JwtUtil {
	
	@Value("${jwt.secret}")
	public String secret;
	
	@Value("${jwt.accessExpiration}")
	public long accessExpiration;
	
	@Value("${jwt.refreshExpiration")
	public long refreshExpiration;
	
	public SecretKey SECRET;
	
	@PostConstruct
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
	
	public String refreshGenerateToken(String username) {
		
		return Jwts.builder()
				.claim(Claims.SUBJECT, username)
				.claim("type", "refresh")
				.claim(Claims.ISSUED_AT, new Date())
				.claim(Claims.EXPIRATION, new Date(
						System.currentTimeMillis() + refreshExpiration))
				.signWith(SECRET)
				.compact();
		
	}
	
	public boolean validateToken(String username) {
		
		try {
			Jwts.parser()
				.verifyWith(SECRET)
				.build()
				.parseSignedClaims(username);
				
			return true;
			
		} catch (ExpiredTokenException e) {
			throw new TokenExpiredException("Token Expired");
		} catch (JwtException | IllegalArgumentException e) {
			throw new InvalidTokenException("Invalid Token");
		}
	}
	
	public Claims extractAllClaims(String username) {
		return Jwts.parser()
					.verifyWith(SECRET)
					.build()
					.parseSignedClaims(username)
					.getPayload();
	}
	

	

}
