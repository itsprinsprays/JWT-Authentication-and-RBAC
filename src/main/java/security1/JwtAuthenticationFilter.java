package security1;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	
	private final JwtUtil jwtUtil;
	private final CustomUserDetailsService customUserDetailsService;
	
	public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
		this.jwtUtil = jwtUtil;
		this.customUserDetailsService = customUserDetailsService;
	}
	
	@Override
	public void doFilterInternal(HttpServletRequest request,
								HttpServletResponse response,
								FilterChain filterChain) throws ServletException, IOException {
	
		String header = request.getHeader("Authorized");
		
		String token = null;
		String username = null;
		
		if(header != null && header.startsWith("Bearer ")) {
			token = header.substring(7);
			
			if(jwtUtil.validateToken(username)) {
				username = jwtUtil.extractUsername(username);
			}
		}
		
		if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
			
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					userDetails, null, userDetails.getAuthorities());
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
		}
		
		filterChain.doFilter(request, response);
								
		
	}

}
