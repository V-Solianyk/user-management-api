package com.example.usermanagementapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.usermanagementapi.dto.UserRequestDto;
import com.example.usermanagementapi.dto.UserResponseDto;
import com.example.usermanagementapi.mapper.UserRequestDtoMapper;
import com.example.usermanagementapi.mapper.UserResponseDtoMapper;
import com.example.usermanagementapi.model.User;
import com.example.usermanagementapi.service.UserService;
import com.example.usermanagementapi.util.SortUtils;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class UserControllerTest {
    private static final LocalDate CORRECT_BIRTHDAY_DATE = LocalDate.of(1995, 5, 2);
    private UserController userController;
    private UserService userService;
    private UserRequestDtoMapper requestDtoMapper;
    private UserResponseDtoMapper responseDtoMapper;
    private UserRequestDto userRequestDto;
    private UserResponseDto userResponseDto;
    private User user;

    @BeforeEach
    void setUp() {
        userRequestDto = new UserRequestDto();
        userResponseDto = new UserResponseDto();
        user = new User();
        user.setId(1L);
        user.setFirstName("Vlad");
        user.setLastName("Duncan");
        user.setEmail("vladDuncan@gmail.com");
        user.setBirthDate(CORRECT_BIRTHDAY_DATE);
        userService = Mockito.mock(UserService.class);
        requestDtoMapper = Mockito.mock(UserRequestDtoMapper.class);
        responseDtoMapper = Mockito.mock(UserResponseDtoMapper.class);
        userController = new UserController(userService, requestDtoMapper, responseDtoMapper);
    }

    @Test
    void create_ok() {
        when(requestDtoMapper.mapToModel(userRequestDto)).thenReturn(user);
        when(userService.create(user)).thenReturn(user);
        when(responseDtoMapper.mapToDto(user)).thenReturn(userResponseDto);
        ResponseEntity<UserResponseDto> responseEntity = userController.create(userRequestDto);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(userResponseDto, responseEntity.getBody());
    }

    @Test
    void get_ok() {
        Long id = 1L;
        when(userService.get(id)).thenReturn(user);
        when(responseDtoMapper.mapToDto(user)).thenReturn(userResponseDto);
        ResponseEntity<UserResponseDto> responseEntity = userController.get(id);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userResponseDto, responseEntity.getBody());
    }

    @Test
    void update_ok() {
        Long id = 1L;
        when(requestDtoMapper.mapToModel(userRequestDto)).thenReturn(user);
        when(userService.update(user, id)).thenReturn(user);
        when(responseDtoMapper.mapToDto(user)).thenReturn(userResponseDto);
        ResponseEntity<UserResponseDto> responseEntity = userController
                .update(id, userRequestDto);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userResponseDto, responseEntity.getBody());
    }

    @Test
    void updateSomeFields() {
        Long id = 1L;
        when(requestDtoMapper.mapToModel(userRequestDto)).thenReturn(user);
        when(userService.update(user, id)).thenReturn(user);
        when(responseDtoMapper.mapToDto(user)).thenReturn(userResponseDto);
        ResponseEntity<UserResponseDto> responseEntity = userController
                .update(id, userRequestDto);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userResponseDto, responseEntity.getBody());
    }

    @Test
    void delete() {
        Long id = 1L;
        ResponseEntity<User> responseEntity = userController.delete(id);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        verify(userService, times(1)).delete(id);
    }

    @Test
    void getAllByBirthDateBetween() {
        final LocalDate from = LocalDate.of(1998, 1, 1);
        final LocalDate to = LocalDate.of(2000, 1, 1);
        int page = 0;
        int count = 10;
        final String sortBy = "id";
        final PageRequest pageRequest = PageRequest.of(page, count, SortUtils.createSort(sortBy));

        User secondUser = new User();
        secondUser.setId(2L);
        secondUser.setBirthDate(LocalDate.of(1999, 5, 2));

        User thirdUser = new User();
        thirdUser.setId(3L);
        thirdUser.setBirthDate(LocalDate.of(1998, 5, 2));

        List<User> users = List.of(secondUser, thirdUser);
        UserResponseDto userResponseDtoSecond = new UserResponseDto();
        userResponseDtoSecond.setId(secondUser.getId());
        UserResponseDto userResponseDtoThird = new UserResponseDto();
        userResponseDtoThird.setId(thirdUser.getId());
        List<UserResponseDto> userResponseDtos = List.of(userResponseDtoSecond,
                userResponseDtoThird);
        when(userService.getAllUsersByBirthDateBetween(from, to, pageRequest))
                .thenReturn(users);
        when(responseDtoMapper.mapToDto(users.get(0))).thenReturn(userResponseDtos.get(0));
        when(responseDtoMapper.mapToDto(users.get(1))).thenReturn(userResponseDtos.get(1));
        ResponseEntity<List<UserResponseDto>> responseEntity = userController
                .getAllByBirthDateBetween(count, page, sortBy, from, to);
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userResponseDtos, responseEntity.getBody());
        assertEquals(userResponseDtos.size(), responseEntity.getBody().size());
        assertEquals(3L, responseEntity.getBody().get(1).getId());
    }
}
