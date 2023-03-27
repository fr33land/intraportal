package org.intraportal.persistence.repository.user;

import org.intraportal.persistence.model.user.UsersRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("UsersRolesRepository")
public interface UsersRolesRepository extends JpaRepository<UsersRoles, Integer> {

    @Modifying
    void deleteAllByUserId(Integer userId);

    @Modifying
    @Query(value = "insert into intraportal.user_roles (user_id, role_id) values (:userId, :roleId)", nativeQuery = true)
    void updateUserRole(@Param("userId") Integer userId, @Param("roleId") Integer roleId);
}