package com.andreidodu.europealibrary.repository.auth;

import com.andreidodu.europealibrary.model.auth.User;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends TransactionalRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String defaultAdminUsername);
}
