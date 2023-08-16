package com.webwarriors.bb.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webwarriors.bb.models.User;
import com.webwarriors.bb.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class AuthController {

	@Autowired
    private UserService userService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	private SecurityContextRepository securityContextRepository =
	        new HttpSessionSecurityContextRepository(); 
	
	private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();


	@PostMapping("/auth")
	public ResponseEntity<?> login(@RequestBody User loginUser, HttpServletRequest request, HttpServletResponse response) {
		try {
	    UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(
	    		loginUser.getEmail(), loginUser.getPassword()); 
	    Authentication authentication = authenticationManager.authenticate(token); 
	    SecurityContext context = securityContextHolderStrategy.createEmptyContext();
	    context.setAuthentication(authentication); 
	    securityContextHolderStrategy.setContext(context);
	    securityContextRepository.saveContext(context, request, response); 
	    System.out.println("authentication.getName()");
	    //If authentication is successful, it will return a 200 OK with user details
		if (authentication.isAuthenticated()) {
			return ResponseEntity.ok(userService.getUserByEmail(loginUser.getEmail()));
		}
		return ResponseEntity.badRequest().body("Could not authenticate");
	} catch (BadCredentialsException e) {
		// If authentication fails due to bad credentials, it returns a 401 Unauthorized
		return ResponseEntity.status(401).body("Invalid credentials");
	} catch (AuthenticationException e) {
		// For other authentication issues, a 400 Bad Request is returned.
		return ResponseEntity.status(400).body("Authentication failed");
	}
}

	@PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return ResponseEntity.ok().body("Logged out successfully");
    }
	
//	@PostMapping("/register")
//    public ResponseEntity<?> register(@RequestBody User user) {
//        try {
//            User registeredUser = userService.addUser(user);
//            return ResponseEntity.ok("User registered successfully!");
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//v2
//	@PostMapping("/login")
//	public ResponseEntity<?> login(@RequestBody User loginUser) {
//	    try {
//	        Authentication auth = authenticationManager.authenticate(
//	                new UsernamePasswordAuthenticationToken(
//	                        loginUser.getEmail(), 
//	                        loginUser.getPassword()
//	                )
//	        );
//	        
//	        // If authentication is successful, store the authentication object 
//	        // in the SecurityContextHolder for maintaining the session
//	        SecurityContextHolder.getContext().setAuthentication(auth);
//	        
//	        
//	        // You can create a UserResponseDTO to avoid returning sensitive or unnecessary data
//	        //UserResponseDTO userResponse = new UserResponseDTO(auth.getName() /* any other safe info */);
//	        
//	        return ResponseEntity.ok(auth.getName());
//	        
//	    } catch (BadCredentialsException e) {
//	        // If authentication fails due to bad credentials, return a 401 Unauthorized
//	        return ResponseEntity.status(401).body("Invalid credentials");
//	    } catch (AuthenticationException e) {
//	        // For other authentication issues, return a 400 Bad Request
//	        return ResponseEntity.status(400).body("Authentication failed");
//	    }
//	}

	//v1
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody User loginUser) {
//        try {
//            Authentication auth = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            loginUser.getEmail(), 
//                            loginUser.getPassword()
//                    )
//            );
//         // If authentication is successful, store the authentication object 
////	        // in the SecurityContextHolder for maintaining the session
//	        SecurityContextHolder.getContext().setAuthentication(auth);
//	        System.out.println("authname"+auth.getName());
//            //If authentication is successful, it will return a 200 OK with user details
//            if (auth.isAuthenticated()) {
//                return ResponseEntity.ok(auth.getPrincipal());
//            }
//            return ResponseEntity.badRequest().body("Could not authenticate");
//        } catch (BadCredentialsException e) {
//        	//If authentication fails due to bad credentials, it returns a 401 Unauthorized
//            return ResponseEntity.status(401).body("Invalid credentials");
//        } catch (AuthenticationException e) {
//        	//For other authentication issues, a 400 Bad Request is returned.
//            return ResponseEntity.status(400).body("Authentication failed");
//        }
//    }

    @GetMapping("/isAuthenticated")
    public ResponseEntity<?> isAuthenticated(Principal principal) {
        if (principal != null) {
        	// Return the username if isAuthenticated
            return ResponseEntity.ok().body(principal.getName());  
        } else {
        	// Return 401 status code.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();  
        }
    }
    
    @GetMapping("/sessionDetails")
    public ResponseEntity<?> getSessionDetails(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return ResponseEntity.ok("Session exists with ID: " + session.getId());
        } else {
            return ResponseEntity.ok("No active session");
        }
    }
    
    @GetMapping("/authDetails")
    public ResponseEntity<?> getAuthDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return ResponseEntity.ok("Authenticated user: " + auth.getName());
        } else {
            return ResponseEntity.ok("No authentication found");
        }
    }

}
