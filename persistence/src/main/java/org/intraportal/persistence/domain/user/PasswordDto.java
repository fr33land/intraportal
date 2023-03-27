package org.intraportal.persistence.domain.user;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class PasswordDto {

    @NotNull
    private Integer userId;

    @NotEmpty
    private String passwordFirst;

    @NotEmpty
    private String passwordSecond;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer id) {
        this.userId = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PasswordDto that = (PasswordDto) o;

        if (!Objects.equals(userId, that.userId)) return false;
        if (!Objects.equals(passwordFirst, that.passwordFirst))
            return false;
        return Objects.equals(passwordSecond, that.passwordSecond);
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (passwordFirst != null ? passwordFirst.hashCode() : 0);
        result = 31 * result + (passwordSecond != null ? passwordSecond.hashCode() : 0);
        return result;
    }
}
