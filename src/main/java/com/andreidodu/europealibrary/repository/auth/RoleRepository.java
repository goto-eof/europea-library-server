package com.andreidodu.europealibrary.repository.auth;

import com.andreidodu.europealibrary.model.auth.Role;
import com.andreidodu.europealibrary.model.auth.User;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends TransactionalRepository<Role, Long> {
}
