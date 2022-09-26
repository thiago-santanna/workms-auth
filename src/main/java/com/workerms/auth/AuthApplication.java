package com.workerms.auth;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.workerms.auth.entity.Permission;
import com.workerms.auth.entity.User;
import com.workerms.auth.repository.PermissionRepository;
import com.workerms.auth.repository.UserRepository;

@SpringBootApplication
public class AuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UserRepository userRepository, PermissionRepository permissionRepository,
			BCryptPasswordEncoder bCryptPasswordEncoder) {
		return args -> {
			initUsers(userRepository, permissionRepository, bCryptPasswordEncoder);
		};
	}

	private void initUsers(UserRepository userRepository, PermissionRepository permissionRepository,
			BCryptPasswordEncoder bCryptPasswordEncoder) {
		
		Permission permission = null;
		Permission findPermission = permissionRepository.findByDescription("Admin");
		if(findPermission != null){
			permission = findPermission;
		}
		else {
			permission = new Permission();
			permission.setDescription("Admin");
			permission = permissionRepository.save(permission);
		}
		
		User admin = new User();
		admin.setUserName("thiagoTeste");
		admin.setAccountNonExpired(true);
		admin.setAccountNonLocked(true);
		admin.setCredentialsNonExpired(true);
		admin.setEnabled(true);
		admin.setPassword(bCryptPasswordEncoder.encode("123456"));
		admin.setPermissions(Arrays.asList(permission));
		User findUser = userRepository.findByUserName("thiagoTeste");
		if(findUser == null) {
			userRepository.save(admin);
		}
	}

}
