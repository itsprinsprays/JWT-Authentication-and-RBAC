package security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.prince.ems.entity.User;
import com.prince.ems.exception.ResourceNotFoundException;
import com.prince.ems.repository.UserRepository;

public class CustomUserDetailsService implements UserDetailsService{
	
	private final UserRepository userRepository;
	
	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) {
		
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException(""));
		
		return org.springframework.security.core.userdetails.User
				.builder()
				.username(user.getUsername())
				.password(user.getPassword())
				.roles(user.getRole().name())
				.build();
		
	}

}
