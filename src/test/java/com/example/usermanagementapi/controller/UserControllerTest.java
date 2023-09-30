package com.example.usermanagementapi.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.usermanagementapi.dto.UserRequestDto;
import com.example.usermanagementapi.dto.UserResponseDto;
import com.example.usermanagementapi.mapper.UserRequestDtoMapper;
import com.example.usermanagementapi.mapper.UserResponseDtoMapper;
import com.example.usermanagementapi.model.User;
import com.example.usermanagementapi.service.UserService;
import com.example.usermanagementapi.util.SortUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    private static final LocalDate CORRECT_BIRTHDAY_DATE = LocalDate.of(1995, 5, 2);
    private UserResponseDto userResponseDto;
    private User user;
    private UserRequestDto userRequestDto;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    @MockBean
    private UserRequestDtoMapper requestDtoMapper;
    @MockBean
    private UserResponseDtoMapper responseDtoMapper;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("Vlad")
                .lastName("TestingLastName")
                .email("vladTest@com.ua")
                .birthDate(CORRECT_BIRTHDAY_DATE).build();

        userRequestDto = UserRequestDto.builder()
                .firstName(user.getLastName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .birthDate(CORRECT_BIRTHDAY_DATE).build();

        userResponseDto = UserResponseDto.builder()
                .id(user.getId())
                .firstName(user.getLastName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .birthDate(CORRECT_BIRTHDAY_DATE).build();
    }

    @Test
    public void create_ok() throws Exception {
        when(requestDtoMapper.mapToModel(userRequestDto)).thenReturn(user);
        when(userService.create(user)).thenReturn(user);
        when(responseDtoMapper.mapToDto(user)).thenReturn(userResponseDto);
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userResponseDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userResponseDto.getId()))
                .andExpect(jsonPath("$.firstName").value(userResponseDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(userResponseDto.getLastName()))
                .andExpect(jsonPath("$.email").value(userResponseDto.getEmail()))
                .andExpect(jsonPath("$.birthDate").value(userResponseDto.getBirthDate()
                        .toString()));
    }

    @Test
    public void create_notValidInputDto_notOk() throws Exception {
        userRequestDto.setFirstName("");
        userRequestDto.setLastName(null);
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_notValidPhoneNumber_notOk() throws Exception {
        userRequestDto.setPhoneNumber("0001");
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error_phoneNumber\":\"Invalid phone number"
                        + " format\"}"));
    }

    @Test
    public void create_notValidEmail_notOk() throws Exception {
        userRequestDto.setEmail("notValidEmail");
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error_email\":\"Invalid email\"}"));
    }

    @Test
    public void create_emptyFirstName_notOk() throws Exception {
        userRequestDto.setFirstName("");
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error_firstName\":\"First name is required\"}"));
    }

    @Test
    public void create_firstNameIsNull_notOk() throws Exception {
        userRequestDto.setFirstName(null);
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error_firstName\":\"First name is required\"}"));
    }

    @Test
    public void create_lastNameIsEmpty_notOk() throws Exception {
        userRequestDto.setLastName("");
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error_lastName\":\"Last name is required\"}"));
    }

    @Test
    public void create_lastNameIsNull_notOk() throws Exception {
        userRequestDto.setLastName(null);
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error_lastName\":\"Last name is required\"}"));
    }

    @Test
    public void create_invalidBirthDateFromFuture_notOk() throws Exception {
        userRequestDto.setBirthDate(LocalDate.of(2050, 1, 5));
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error_birthDate\":\"Birth date must be in the"
                        + " past\"}"));
    }

    @Test
    public void create_validPhoneNumber_ok() throws Exception {
        userRequestDto.setPhoneNumber("380961276845");
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    public void get_ok() throws Exception {
        Long id = 1L;
        when(userService.get(id)).thenReturn(user);
        when(responseDtoMapper.mapToDto(user)).thenReturn(userResponseDto);
        mvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponseDto.getId()))
                .andExpect(jsonPath("$.firstName").value(userResponseDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(userResponseDto.getLastName()))
                .andExpect(jsonPath("$.email").value(userResponseDto.getEmail()))
                .andExpect(jsonPath("$.birthDate").value(userResponseDto.getBirthDate()
                        .toString()));
    }

    @Test
    public void get_notNotExistUserByIndex_notOk() throws Exception {
        Long id = 888L;
        when(userService.get(id))
                .thenThrow(EntityNotFoundException.class);
        mvc.perform(get("/users/888")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void update_ok() throws Exception {
        Long id = 1L;
        when(requestDtoMapper.mapToModel(userRequestDto)).thenReturn(user);
        when(userService.update(user, id)).thenReturn(user);
        when(responseDtoMapper.mapToDto(user)).thenReturn(userResponseDto);
        mvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponseDto.getId()))
                .andExpect(jsonPath("$.firstName").value(userResponseDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(userResponseDto.getLastName()))
                .andExpect(jsonPath("$.email").value(userResponseDto.getEmail()))
                .andExpect(jsonPath("$.birthDate").value(userResponseDto.getBirthDate()
                        .toString()));
    }

    @Test
    public void update_notValidPhoneNumber_notOk() throws Exception {
        final Long id = 1L;
        userRequestDto.setPhoneNumber("0001");
        when(requestDtoMapper.mapToModel(userRequestDto)).thenReturn(user);
        user.setPhoneNumber(userRequestDto.getPhoneNumber());
        when(userService.update(user, id)).thenReturn(user);
        when(responseDtoMapper.mapToDto(user)).thenReturn(userResponseDto);
        mvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error_phoneNumber\":\"Invalid phone number"
                        + " format\"}"));
    }

    @Test
    public void update_notValidEmail_notOk() throws Exception {
        final Long id = 1L;
        userRequestDto.setEmail("NotValidEmail");
        when(requestDtoMapper.mapToModel(userRequestDto)).thenReturn(user);
        user.setEmail(userRequestDto.getEmail());
        when(userService.update(user, id)).thenReturn(user);
        when(responseDtoMapper.mapToDto(user)).thenReturn(userResponseDto);
        mvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error_email\":\"Invalid email\"}"));
    }

    @Test
    public void update_emptyFirstName_notOk() throws Exception {
        final Long id = 1L;
        userRequestDto.setFirstName("");
        when(requestDtoMapper.mapToModel(userRequestDto)).thenReturn(user);
        user.setEmail(userRequestDto.getEmail());
        when(userService.update(user, id)).thenReturn(user);
        when(responseDtoMapper.mapToDto(user)).thenReturn(userResponseDto);
        mvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error_firstName\":\"First name is required\"}"));
    }

    @Test
    public void update_firstNameIsNull_notOk() throws Exception {
        final Long id = 1L;
        userRequestDto.setFirstName(null);
        when(requestDtoMapper.mapToModel(userRequestDto)).thenReturn(user);
        user.setEmail(userRequestDto.getEmail());
        when(userService.update(user, id)).thenReturn(user);
        when(responseDtoMapper.mapToDto(user)).thenReturn(userResponseDto);
        mvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error_firstName\":\"First name is required\"}"));
    }

    @Test
    public void update_lastNameIsEmpty_notOk() throws Exception {
        final Long id = 1L;
        userRequestDto.setLastName("");
        when(requestDtoMapper.mapToModel(userRequestDto)).thenReturn(user);
        user.setLastName(userRequestDto.getLastName());
        when(userService.update(user, id)).thenReturn(user);
        when(responseDtoMapper.mapToDto(user)).thenReturn(userResponseDto);
        mvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error_lastName\":\"Last name is required\"}"));
    }

    @Test
    public void update_lastNameIsNull_notOk() throws Exception {
        final Long id = 1L;
        userRequestDto.setLastName(null);
        when(requestDtoMapper.mapToModel(userRequestDto)).thenReturn(user);
        user.setLastName(userRequestDto.getLastName());
        when(userService.update(user, id)).thenReturn(user);
        when(responseDtoMapper.mapToDto(user)).thenReturn(userResponseDto);
        mvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error_lastName\":\"Last name is required\"}"));
    }

    @Test
    public void update_invalidBirthDateFromFuture_notOk() throws Exception {
        final Long id = 1L;
        userRequestDto.setBirthDate(LocalDate.of(2100, 2,2));
        when(requestDtoMapper.mapToModel(userRequestDto)).thenReturn(user);
        user.setBirthDate(userRequestDto.getBirthDate());
        when(userService.update(user, id)).thenReturn(user);
        when(responseDtoMapper.mapToDto(user)).thenReturn(userResponseDto);
        mvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error_birthDate\":\"Birth date must be in the"
                        + " past\"}"));
    }

    @Test
    public void update_notValidInputDto_notOk() throws Exception {
        userRequestDto.setFirstName("");
        userRequestDto.setLastName("");
        mvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void partiallyUpdateUser_ok() throws Exception {
        Long id = 1L;
        when(requestDtoMapper.mapToModel(userRequestDto)).thenReturn(user);
        when(userService.particularUpdateUser(user, id)).thenReturn(user);
        when(responseDtoMapper.mapToDto(user)).thenReturn(userResponseDto);
        mvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userResponseDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponseDto.getId()))
                .andExpect(jsonPath("$.firstName").value(userResponseDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(userResponseDto.getLastName()))
                .andExpect(jsonPath("$.email").value(userResponseDto.getEmail()))
                .andExpect(jsonPath("$.birthDate").value(userResponseDto.getBirthDate()
                        .toString()));
    }

    @Test
    public void delete_ok() throws Exception {
        Long id = 1L;
        userService.delete(id);
        mvc.perform(delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void getAllByBirthDateBetween_ok() throws Exception {
        final LocalDate from = LocalDate.of(1998, 1, 1);
        final LocalDate to = LocalDate.of(2005, 1, 1);
        int page = 0;
        int count = 10;
        final String sortBy = "id";
        final PageRequest pageRequest = PageRequest.of(page, count, SortUtils.createSort(sortBy));

        User secondUser = User.builder()
                .id(2L)
                .birthDate(LocalDate.of(1999, 10, 3))
                .build();

        User thirdUser = User.builder()
                .id(3L)
                .birthDate(LocalDate.of(2001, 2, 3))
                .build();

        final List<User> users = List.of(secondUser, thirdUser);
        UserResponseDto userResponseDtoSecond = new UserResponseDto();
        userResponseDtoSecond.setId(secondUser.getId());
        userResponseDtoSecond.setBirthDate(secondUser.getBirthDate());

        UserResponseDto userResponseDtoThird = new UserResponseDto();
        userResponseDtoThird.setId(thirdUser.getId());
        userResponseDtoThird.setBirthDate(thirdUser.getBirthDate());

        List<UserResponseDto> userResponseDtos = List.of(userResponseDtoSecond,
                userResponseDtoThird);
        when(userService.getAllUsersByBirthDateBetween(from, to, pageRequest)).thenReturn(users);
        when(responseDtoMapper.mapToDto(users.get(0))).thenReturn(userResponseDtos.get(0));
        when(responseDtoMapper.mapToDto(users.get(1))).thenReturn(userResponseDtos.get(1));
        mvc.perform(get("/users")
                        .param("from", from.toString())
                        .param("to", to.toString())
                        .param("count", "10")
                        .param("page", "0")
                        .param("sortBy", "id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(userResponseDtos.size()))
                .andExpect(jsonPath("$[0].id").value(userResponseDtos.get(0).getId()))
                .andExpect(jsonPath("$[0].birthDate").value(userResponseDtos.get(0)
                        .getBirthDate().toString()));
    }

    @Test
    public void getAllByBirthDateBetween_inputDateIncorrect_ok() throws Exception {
        final LocalDate from = LocalDate.of(2001, 1, 1);
        final LocalDate to = LocalDate.of(1999, 1, 1);
        int page = 0;
        int count = 10;
        final String sortBy = "id";
        final PageRequest pageRequest = PageRequest.of(page, count, SortUtils.createSort(sortBy));

        when(userService.getAllUsersByBirthDateBetween(from, to, pageRequest))
                .thenThrow(IllegalArgumentException.class);
        mvc.perform(get("/users")
                        .param("from", from.toString())
                        .param("to", to.toString())
                        .param("count", "10")
                        .param("page", "0")
                        .param("sortBy", "id"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("{\"httpStatus\":\"BAD_REQUEST\""
                        + ",\"errorMessage\":\"\\\"from\\\" date should be before "
                        + "\\\"to\\\" date.\"}")));
    }
}
