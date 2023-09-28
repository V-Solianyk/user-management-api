package com.example.usermanagementapi.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.usermanagementapi.model.User;
import com.example.usermanagementapi.repository.UserRepository;
import com.example.usermanagementapi.service.impl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;

class UserServiceTest {
    private static final LocalDate CORRECT_BIRTHDAY_DATE = LocalDate.of(1995, 5, 2);
    private static final LocalDate INCORRECT_BIRTHDAY_DATE = LocalDate.of(2018, 5, 2);
    private User user;
    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("Vlad");
        user.setLastName("Duncan");
        user.setEmail("vladDuncan@gmail.com");
        user.setBirthDate(CORRECT_BIRTHDAY_DATE);
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void create_ok() {
        when(userRepository.save(user)).thenReturn(user);
        User actualUser = userService.create(user);
        assertEquals(1L, actualUser.getId());
        assertEquals("Vlad", actualUser.getFirstName());
        assertEquals("Duncan", actualUser.getLastName());
        assertEquals(CORRECT_BIRTHDAY_DATE, actualUser.getBirthDate());
        assertEquals("vladDuncan@gmail.com", actualUser.getEmail());
    }

    //    @Test
    //    void create_withAgeLessThanAvailable_notOk() {
    //        String message = "Can't register the user who is younger than 18 years old";
    //        user.setBirthDate(INCORRECT_BIRTHDAY_DATE);
    //
    //        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
    //                () -> userService.create(user));
    //        assertEquals(message, exception.getMessage());
    //    }

    @Test
    void create_withAlreadyExistUser_notOk() {
        String existEmail = "vladDuncan@gmail.com";
        String message = "A user with this email already exists.";
        when(userRepository.findUserByEmail(existEmail)).thenReturn(Optional.of(user));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.create(user));
        assertEquals(message, exception.getMessage());
    }

    @Test
    void get_ok() {
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        User actualUser = userService.get(id);
        assertEquals(1L, actualUser.getId());
        assertEquals("Vlad", actualUser.getFirstName());
        assertEquals("Duncan", actualUser.getLastName());
        assertEquals(CORRECT_BIRTHDAY_DATE, actualUser.getBirthDate());
        assertEquals("vladDuncan@gmail.com", actualUser.getEmail());
    }

    @Test
    void get_withNotExistId_notOk() {
        Long id = 999L;
        String message = "User not found with this id: " + id;
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.get(id));
        assertEquals(message, exception.getMessage());
    }

    @Test
    void update_ok() {
        final Long id = 1L;
        User updateUser = new User();
        updateUser.setFirstName("Victor");
        updateUser.setLastName("Don");
        updateUser.setEmail("VictorDon@gmail.com");
        updateUser.setBirthDate(CORRECT_BIRTHDAY_DATE);
        updateUser.setAddress("Mazepa 117, Ivano-Frankivsk, Ukraine");
        updateUser.setPhoneNumber("+380991102224");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        userService.update(updateUser, id);
        assertEquals(1L, user.getId());
        assertEquals("Victor", user.getFirstName());
        assertEquals("Don", user.getLastName());
        assertEquals("VictorDon@gmail.com", user.getEmail());
        assertEquals("Mazepa 117, Ivano-Frankivsk, Ukraine", user.getAddress());
        assertEquals("+380991102224", user.getPhoneNumber());
    }

    @Test
    void update_withNotExistId_notOk() {
        Long id = 500L;
        String message = "User not found with this id: " + id;
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.get(id));
        assertEquals(message, exception.getMessage());
    }

    //    @Test
    //    void update_withAgeLessThanAvailable_notOk() {
    //        Long id = 1L;
    //        User newUser = new User();
    //        newUser.setBirthDate(INCORRECT_BIRTHDAY_DATE);
    //        newUser.setFirstName("Victor");
    //        newUser.setLastName("Don");
    //        newUser.setEmail("VictorDon@gmail.com");
    //        String message = "Can't register the user who is younger than 18 years old";
    //        when(userRepository.findById(id)).thenReturn(Optional.of(user));
    //        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
    //                () -> userService.update(newUser, id));
    //        assertEquals(message, exception.getMessage());
    //    }

    @Test
    void delete_withNotExistId_notOk() {
        Long id = 333L;
        String message = "User not found with this id: " + id;
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.get(id));
        assertEquals(message, exception.getMessage());
    }

    @Test
    void delete_ok() {
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        assertDoesNotThrow(() -> userService.delete(id));
        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    void getAllUsersByBirthDateBetween_ok() {
        final LocalDate from = LocalDate.of(1900, 1, 1);
        final LocalDate to = LocalDate.of(1999, 1, 1);

        User secondUser = new User();
        secondUser.setId(2L);
        secondUser.setBirthDate(LocalDate.of(2000, 5, 2));
        secondUser.setFirstName("TestName1");
        secondUser.setLastName("Test1");
        secondUser.setEmail("test1@gmail.com");

        User thirdUser = new User();
        thirdUser.setId(3L);
        thirdUser.setBirthDate(LocalDate.of(1991, 5, 2));
        thirdUser.setFirstName("TestName2");
        thirdUser.setLastName("Test2");
        thirdUser.setEmail("test2@gmail.com");

        PageRequest pageRequest = PageRequest.of(0, 10);
        when(userRepository.findByBirthDateBetween(from, to, pageRequest))
                .thenReturn(List.of(user, thirdUser));

        List<User> users = userService.getAllUsersByBirthDateBetween(from, to, pageRequest);
        assertNotNull(users);
        assertEquals(2, users.size());
    }
}
