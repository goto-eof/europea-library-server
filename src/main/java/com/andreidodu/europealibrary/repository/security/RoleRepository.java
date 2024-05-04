package com.andreidodu.europealibrary.repository.security;

import com.andreidodu.europealibrary.model.security.Authority;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;

public interface RoleRepository extends TransactionalRepository<Authority, Long> {
}
