package com.webapp08.pujahoy.security;

    import java.util.ArrayList;
    import java.util.List;
    
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.core.authority.SimpleGrantedAuthority;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.security.core.userdetails.UserDetailsService;
    import org.springframework.security.core.userdetails.UsernameNotFoundException;
    import org.springframework.stereotype.Service;
    import com.webapp08.pujahoy.model.Usuario; 
    import com.webapp08.pujahoy.repository.UsuarioRepository; 
    
    @Service
    public class RepositoryUserDetailsService implements UserDetailsService {
    
        @Autowired
        private UsuarioRepository usuarioRepository; // Usar tu repositorio de usuario
    
        @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException { 
    
            Usuario user = usuarioRepository.findByContacto(email) 
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    
            List<GrantedAuthority> roles = new ArrayList<>();
            for (String role : user.getRoles()) {
                roles.add(new SimpleGrantedAuthority("ROLE_" + role)); // Prefijo ROLE_
            }
    
            return new org.springframework.security.core.userdetails.User(user.getContacto(), 
                    user.getPass(), roles); 
        }
    }   

