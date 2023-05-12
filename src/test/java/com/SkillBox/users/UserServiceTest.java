package com.SkillBox.users;

import com.SkillBox.users.entity.Gender;
import com.SkillBox.users.entity.User;
import com.SkillBox.users.repository.GenderRepository;
import com.SkillBox.users.repository.UserRepository;
import com.SkillBox.users.service.UsersService;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    private final static String MALE_GENDER_DESCRIPTION = "Male";
    private final static Gender MALE_GENDER = new Gender(1L, MALE_GENDER_DESCRIPTION);

    @Autowired
    private UsersService usersService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private GenderRepository genderRepository;
    @MockBean
    private EntityManager entityManager;
    @Mock
    private Session session;
    @Mock
    private Filter filter;

    @Test
    public void saveUser_Success() {
        //given
        User userForSave = getCommonUser();
        User userFromDB = getCommonUser();
        userFromDB.setId(1L);
        userFromDB.setGender(MALE_GENDER);
        when(genderRepository.findOrCreateByDescription(anyString()))
                .thenReturn(MALE_GENDER);
        when(userRepository.save(any(User.class)))
                .thenReturn(userFromDB);
        //when
        var result = usersService.saveUser(userForSave);
        //then
        assertEquals(userFromDB, result);
        verify(genderRepository, times(1)).findOrCreateByDescription(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deleteUserById_ShouldThrowException_WhenUserNotFound() {
        //given
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        //then
        assertThrows(ResponseStatusException.class, () -> usersService.deleteUserById(userId));
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserFound() {
        //given
        Long userId = 1L;

        User userFromDB = getCommonUser();
        userFromDB.setId(1L);
        userFromDB.setGender(MALE_GENDER);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(userFromDB));

        //when
        User result = usersService.getUserById(userId);

        //then
        assertEquals(userFromDB, result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        //given
        Long userId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        //then
        assertThrows(ResponseStatusException.class, () -> usersService.getUserById(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void updateUserById_ShouldThrowException_WhenUserNotFound() {
        //given
        Long userId = 1L;
        User user = new User();

        when(userRepository.existsById(userId)).thenReturn(false);

        //when
        assertThrows(ResponseStatusException.class, () -> usersService.updateUserById(userId, user));
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findAllUsers_shouldReturnAllUsers_whenIsDeletedIsFalse() {
        //given
        List<User> users = new ArrayList<>();
        users.add(getCommonUser());
        users.add(getCommonUser());
        when(userRepository.findAll()).thenReturn(users);
        when(session.enableFilter(anyString())).thenReturn(filter);
        when(entityManager.unwrap(Session.class)).thenReturn(session);

        //when
        List<User> result = usersService.findAllUsers(false);

        //then
        verify(session, times(1)).enableFilter("deletedUserFilter");
        verify(filter, times(1)).setParameter("isDeleted", false);
        verify(session, times(1)).disableFilter("deletedUserFilter");
        verify(userRepository, times(1)).findAll();
        assertEquals(users, result);
    }

    @Test
    void findAllUsers_shouldReturnDeletedUsers_whenIsDeletedIsTrue() {
        // given
        List<User> deletedUsers = new ArrayList<>();
        deletedUsers.add(getCommonUser());
        deletedUsers.add(getCommonUser());
        when(userRepository.findAll()).thenReturn(deletedUsers);
        when(session.enableFilter(anyString())).thenReturn(filter);
        when(entityManager.unwrap(Session.class)).thenReturn(session);

        // when
        List<User> result = usersService.findAllUsers(true);

        // then
        verify(session, times(1)).enableFilter("deletedUserFilter");
        verify(filter, times(1)).setParameter("isDeleted", true);
        verify(session, times(1)).disableFilter("deletedUserFilter");
        verify(userRepository, times(1)).findAll();
        assertEquals(deletedUsers, result);
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
