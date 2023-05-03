package com.SkillBox.users;

import com.SkillBox.users.entity.Gender;
import com.SkillBox.users.entity.User;
import com.SkillBox.users.repository.GenderRepository;
import com.SkillBox.users.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UsersApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsersApplication.class, args);
	}

	@Bean
	CommandLineRunner demoJpa(UserRepository userRepository, GenderRepository genderRepository) {
		return (args) -> {
			userRepository.deleteAll();
			genderRepository.deleteAll();

			Gender male = new Gender();
			male.setDescription("Male");
			genderRepository.save(male);

			User user1 = new User();
			user1.setFirstName("Ivan");
			user1.setLastName("Ivanov");
			user1.setGender(male);
			user1.setUserNickname("Ivan007");
			user1.setEmail("Ivan007@mail.ru");
			userRepository.save(user1);

			for (User user: userRepository.findAll()) {
				System.out.println(user);
			}

			for (Gender user: genderRepository.findAll()) {
				System.out.println(user);
			}
		};
	}
}
