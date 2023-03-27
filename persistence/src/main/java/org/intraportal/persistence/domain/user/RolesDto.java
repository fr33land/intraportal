package org.intraportal.persistence.domain.user;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

public class RolesDto {

    @NotNull
    private Integer userId;

    private List<RoleDto> list;

    public RolesDto() {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<RoleDto> getList() {
        return list;
    }

    public void setList(List<RoleDto> list) {
        this.list = list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RolesDto rolesDto = (RolesDto) o;

        if (!Objects.equals(userId, rolesDto.userId)) return false;
        return Objects.equals(list, rolesDto.list);
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (list != null ? list.hashCode() : 0);
        return result;
    }
}
