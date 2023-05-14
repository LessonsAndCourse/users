import com.SkillBox.users.UsersApplication;
import com.SkillBox.users.entity.Gender;
import com.SkillBox.users.entity.User;
import com.SkillBox.users.repository.GenderRepository;
import com.SkillBox.users.repository.UserRepository;
import com.SkillBox.users.service.UsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = UsersApplication.class)
public class UserModuleTest {

    private final static String MALE_GENDER_DESCRIPTION = "Male";
    private final static Gender MALE_GENDER = new Gender(1L, MALE_GENDER_DESCRIPTION);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UsersService usersService;

    @Autowired
    private GenderRepository genderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    private static final PostgreSQLContainer<PostgresContainerWrapper> postgresContainer = new PostgresContainerWrapper();

    @DynamicPropertySource
    public static void initSystemParams(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeEach
    public void cleanDB() {
        userRepository.deleteAll();
    }

    @Test
    public void saveUser_Success() throws Exception {
        // given
        User userForSave = getCommonUser();
        userForSave.setUserNickname("some");
        userForSave.setEmail("some");

        User savedUser = getCommonUser();
        savedUser.setUserNickname("some");
        savedUser.setEmail("some");

        genderRepository.save(MALE_GENDER);

        // when/then
        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(userForSave)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(savedUser.getFirstName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(savedUser.getLastName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userNickname").value(savedUser.getUserNickname()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(savedUser.getEmail()));
    }

    @Test
    public void deleteUserById_Success() throws Exception {
        // given
        Long userId = 1L;
        User user = getCommonUser();
        user.setId(userId);

        usersService.saveUser(user);

        // when/then
        mockMvc.perform(MockMvcRequestBuilders.delete("/user/{id}", userId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void getUserById_Success() throws Exception {
        // given
        Long userId = 1L;
        User user = getCommonUser();
        user.setId(userId);

        usersService.saveUser(user);

        // when/then
        mockMvc.perform(MockMvcRequestBuilders.get("/user/{id}", userId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(user.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(user.getLastName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userNickname").value(user.getUserNickname()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    public void findAllUsers_DELETED_FALSE_Success() throws Exception {
        // given
        User user1 = getCommonUser();
        user1.setId(1L);
        user1.setGender(MALE_GENDER);

        User user2 = getCommonUser();
        user2.setId(2L);
        user2.setGender(MALE_GENDER);
        user2.setUserNickname("someNick");
        user2.setEmail("someEmail");

        List<User> users = List.of(user1, user2);

        genderRepository.save(MALE_GENDER);
        userRepository.saveAll(users);

        // when/then
        mockMvc.perform(MockMvcRequestBuilders.get("/user?isDeleted=false"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(users.size()));
    }

    @Test
    public void updateUserById_Success() throws Exception {
        // given
        Long userId = 1L;
        User savedUser = getCommonUser();
        savedUser.setId(userId);

        User updatedUser = getCommonUser();
        updatedUser.setId(1L);
        updatedUser.setUserNickname("SuoerNickName");
        updatedUser.setEmail("superEmail");

        usersService.saveUser(savedUser);

        // when/then
        mockMvc.perform(MockMvcRequestBuilders.put("/user/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(updatedUser.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(updatedUser.getFirstName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(updatedUser.getLastName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userNickname").value(updatedUser.getUserNickname()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(updatedUser.getEmail()));
    }

    private User getCommonUser() {
        User user = new User();
        user.setFirstName("Ivan");
        user.setLastName("Ivanov");
        user.setGender(new Gender(MALE_GENDER_DESCRIPTION));
        user.setUserNickname("Ivan");
        user.setEmail("Ivan@.com");
        return user;
    }
}
