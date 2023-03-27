package org.intraportal.api.model.mappers;

import org.intraportal.persistence.domain.user.RoleDto;
import org.intraportal.persistence.domain.user.UserDto;
import org.intraportal.persistence.model.user.User;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.intraportal.api.model.mappers.DateTimeMapper.toLocalDateTime;
import static org.intraportal.api.model.mappers.DateTimeMapper.toOffsetDateTime;

public class UserMapper {

    public static UserDto userToUserDto(User u, Boolean clearPassword) {
        var udto = new UserDto();
        udto.setId(u.getId());
        udto.setUsername(u.getUsername());
        udto.setEmail(u.getEmail());
        udto.setPasswordFirst(clearPassword ? null : u.getPassword());
        udto.setFirstName(u.getFirstName());
        udto.setLastName(u.getLastName());
        udto.setEnabled(u.getEnabled());
        udto.setLastLogin(toLocalDateTime(u.getLastLogin()));
        udto.setCreated(toLocalDateTime(u.getCreatedDate()));
        return udto;
    }

    public static User userDtoToUser(UserDto udto, Boolean clearPassword) {
        var u = new User();
        u.setId(udto.getId());
        u.setUsername(udto.getUsername() == null ? null : udto.getUsername().trim().toLowerCase(Locale.ROOT));
        u.setEmail(udto.getEmail() == null ? null : udto.getEmail().trim().toLowerCase(Locale.ROOT));
        u.setPassword(clearPassword ? null : udto.getPasswordFirst());
        u.setFirstName(udto.getFirstName() == null ? null : udto.getFirstName().trim());
        u.setLastName(udto.getLastName() == null ? null : udto.getLastName().trim());
        u.setEnabled(udto.getEnabled());
        u.setLastLogin(toOffsetDateTime(udto.getLastLogin()));
        u.setCreatedDate(toOffsetDateTime(udto.getCreated()));
        return u;
    }

    public static UserDto userToUserDtoSanitized(User u) {
        var udto = new UserDto();
        udto.setUsername(u.getUsername());
        udto.setEmail(u.getEmail());
        udto.setFirstName(u.getFirstName());
        udto.setLastName(u.getLastName());
        udto.setEnabled(Boolean.FALSE);

        List<RoleDto> rolesDto = u.getRoles().stream().map(r -> {
            RoleDto rd = new RoleDto();
            rd.setName(r.getName());
            rd.setDescription(r.getDescription());
            return rd;
        }).collect(Collectors.toList());
        udto.setRolesDto(rolesDto);

        return udto;
    }
}
