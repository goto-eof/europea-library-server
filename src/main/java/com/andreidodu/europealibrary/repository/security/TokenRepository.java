package com.andreidodu.europealibrary.repository.security;

import com.andreidodu.europealibrary.model.security.Token;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;

public interface TokenRepository extends TransactionalRepository<Token, Long> {
}
