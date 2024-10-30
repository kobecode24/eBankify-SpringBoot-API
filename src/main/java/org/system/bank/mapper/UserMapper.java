package org.system.bank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.system.bank.dto.request.UserRegistrationRequest;
import org.system.bank.dto.response.UserResponse;
import org.system.bank.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface UserMapper {

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    User toEntity(UserRegistrationRequest request);

    @Mapping(target = "accounts", source = "accounts")
    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);
}