package com.example.usermanagementapi.mapper;

import com.example.usermanagementapi.config.MyMapperConfig;
import com.example.usermanagementapi.dto.UserResponseDto;
import com.example.usermanagementapi.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MyMapperConfig.class)
public interface UserResponseDtoMapper {
    UserResponseDto mapToDto(User user);
}
