package org.intraportal.persistence.repository.user;

import org.intraportal.persistence.model.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("RoleRepository")
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role findByName(String roleName);
}