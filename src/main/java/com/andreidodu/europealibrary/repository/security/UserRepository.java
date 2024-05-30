package com.andreidodu.europealibrary.repository.security;

import com.andreidodu.europealibrary.model.security.User;
import com.andreidodu.europealibrary.repository.CustomUserRepository;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;

import java.util.Optional;

public interface UserRepository extends TransactionalRepository<User, Long>, CustomUserRepository {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByResetToken(String resetToken);

}
