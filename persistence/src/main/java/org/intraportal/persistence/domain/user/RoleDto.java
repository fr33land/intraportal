package org.intraportal.persistence.domain.user;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class RoleDto {

    @NotNull
    private Integer id;

    @NotEmpty
    private String name;

    private String description;

    private boolean checked;

    public RoleDto() {
    }

    public RoleDto(Integer id) {
        this.id = id;
    }

    public RoleDto(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public RoleDto(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public RoleDto(Integer id, String name, String description, boolean checked) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.checked = checked;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
