package com.andreidodu.europealibrary.repository.auth;

import com.andreidodu.europealibrary.model.auth.User;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UserRepository extends TransactionalRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByResetToken(String resetToken);
}
