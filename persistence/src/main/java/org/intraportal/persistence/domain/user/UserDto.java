package org.intraportal.persistence.domain.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class UserDto {

    private Integer id;

    @NotEmpty
    @Size(max = 250)
    private String username;

    @Size(max = 250)
    private String passwordFirst;

    @Size(max = 250)
    private String passwordSecond;

    private Boolean enabled = false;

    @Size(max = 250)
    private String firstName;

    @Size(max = 250)
    private String lastName;

    @NotEmpty
    @Email
    @Size(max = 250)
    private String email;

    private List<RoleDto> rolesDto;

    private LocalDateTime created;

    private LocalDateTime lastLogin;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordFirst() {
        return passwordFirst;
    }

    public void setPasswordFirst(String passwordFirst) {
        this.passwordFirst = passwordFirst;
    }

    public String getPasswordSecond() {
        return passwordSecond;
    }

    public void setPasswordSecond(String passwordSecond) {
        this.passwordSecond = passwordSecond;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public List<RoleDto> getRolesDto() {
        return rolesDto;
    }

    public void setRolesDto(List<RoleDto> rolesDto) {
        this.rolesDto = rolesDto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDto userDto = (UserDto) o;

        if (!Objects.equals(id, userDto.id)) return false;
        if (!Objects.equals(username, userDto.username)) return false;
        if (!Objects.equals(passwordFirst, userDto.passwordFirst)) return false;
        if (!Objects.equals(passwordSecond, userDto.passwordSecond)) return false;
        if (!Objects.equals(enabled, userDto.enabled)) return false;
        if (!Objects.equals(firstName, userDto.firstName)) return false;
        if (!Objects.equals(lastName, userDto.lastName)) return false;
        return Objects.equals(email, userDto.email);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (passwordFirst != null ? passwordFirst.hashCode() : 0);
        result = 31 * result + (passwordSecond != null ? passwordSecond.hashCode() : 0);
        result = 31 * result + (enabled != null ? enabled.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }
}
