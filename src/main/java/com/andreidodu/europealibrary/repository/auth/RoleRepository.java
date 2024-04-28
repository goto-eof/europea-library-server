package com.andreidodu.europealibrary.repository.auth;

import com.andreidodu.europealibrary.model.auth.Authority;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;
import org.springframework.stereotype.Repository;

public interface RoleRepository extends TransactionalRepository<Authority, Long> {
}
