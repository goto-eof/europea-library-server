package com.andreidodu.europealibrary.repository.security;

import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.PairDTO;
import com.andreidodu.europealibrary.model.security.User;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends TransactionalRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByResetToken(String resetToken);

    PairDTO<List<User>, Long> retrieveAllCursored(CommonCursoredRequestDTO commonCursoredRequestDTO);
}
