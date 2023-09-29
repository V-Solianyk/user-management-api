package com.example.usermanagementapi.controller;

import com.example.usermanagementapi.dto.UserRequestDto;
import com.example.usermanagementapi.dto.UserResponseDto;
import com.example.usermanagementapi.mapper.UserRequestDtoMapper;
import com.example.usermanagementapi.mapper.UserResponseDtoMapper;
import com.example.usermanagementapi.model.User;
import com.example.usermanagementapi.service.UserService;
import com.example.usermanagementapi.util.SortUtils;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRequestDtoMapper requestDtoMapper;
    private final UserResponseDtoMapper responseDtoMapper;

    @PostMapping
    ResponseEntity<UserResponseDto> create(@RequestBody @Valid UserRequestDto userRequestDto) {
        User user = userService.create(requestDtoMapper.mapToModel(userRequestDto));
        return new ResponseEntity<>((responseDtoMapper.mapToDto(user)), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    ResponseEntity<UserResponseDto> get(@PathVariable Long id) {
        User user = userService.get(id);
        return ResponseEntity.ok(responseDtoMapper.mapToDto(user));
    }

    @PutMapping("/{id}")
    ResponseEntity<UserResponseDto> update(@PathVariable Long id,
                                           @RequestBody @Valid UserRequestDto userRequestDto) {
        User user = requestDtoMapper.mapToModel(userRequestDto);
        return ResponseEntity.ok(responseDtoMapper.mapToDto(userService.update(user, id)));
    }

    @PatchMapping("/{id}")
    ResponseEntity<UserResponseDto> partiallyUpdateUser(
            @PathVariable Long id, @RequestBody UserRequestDto userRequestDto) {
        User user = requestDtoMapper.mapToModel(userRequestDto);
        return ResponseEntity.ok(responseDtoMapper.mapToDto(userService
                .particularUpdateUser(user, id)));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<User> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    ResponseEntity<List<UserResponseDto>> getAllByBirthDateBetween(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "id") String sortBy, @RequestParam LocalDate from,
            @RequestParam LocalDate to) {
        checkInputDate(from, to);
        Sort sort = SortUtils.createSort(sortBy);
        PageRequest pageRequest = PageRequest.of(page, count, sort);
        List<User> users = userService.getAllUsersByBirthDateBetween(from, to, pageRequest);
        return ResponseEntity.ok(users.stream()
                .map(responseDtoMapper::mapToDto)
                .collect(Collectors.toList()));
    }

    private void checkInputDate(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("\"from\" date should be before \"to\" date.");
        }
    }
}
