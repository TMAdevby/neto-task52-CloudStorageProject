
package com.example.netotask52cloudstorageproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.example.netotask52cloudstorageproject.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);

    boolean existsByLogin(String login);
}