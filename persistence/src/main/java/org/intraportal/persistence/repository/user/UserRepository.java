package org.intraportal.persistence.repository.user;

import org.intraportal.persistence.model.user.User;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository("UserRepository")
public interface UserRepository extends DataTablesRepository<User, Integer> {

    User findByUsername(String username);

    User findByEmail(String email);

    @Modifying
    @Query("update User u set u.lastLogin = :now where u.username = :username")
    void updateUserLastLogin(@Param("username") String username, @Param("now") OffsetDateTime now);

    @Modifying
    @Query("update User u set u.password = :password where u.id = :id")
    void updateUserPassword(@Param("id") Integer id, @Param("password") String password);
}