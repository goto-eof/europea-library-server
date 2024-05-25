package com.andreidodu.europealibrary.repository.security;

import com.andreidodu.europealibrary.model.security.Token;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends TransactionalRepository<Token, Long> {
    Optional<Token> findByTokenAndValidFlag(String token, boolean validFlag);

    List<Token> findByTokenAndAgentIdAndValidFlag(String token, String agentId, boolean validFlag);
}
