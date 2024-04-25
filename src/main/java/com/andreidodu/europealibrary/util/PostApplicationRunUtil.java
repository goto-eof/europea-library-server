package com.andreidodu.europealibrary.util;

import com.andreidodu.europealibrary.constants.AuthConst;
import com.andreidodu.europealibrary.model.auth.Role;
import com.andreidodu.europealibrary.model.auth.User;
import com.andreidodu.europealibrary.repository.auth.RoleRepository;
import com.andreidodu.europealibrary.repository.auth.UserRepository;
import com.andreidodu.europealibrary.service.CacheLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostApplicationRunUtil {
    private final CacheLoader cacheLoader;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RoleRepository roleRepository;
    @Value("${com.andreidodu.europea-library.default-admin-username}")
    private String defaultAdminUsername;

    @Value("${com.andreidodu.europea-library.default-admin-email}")
    private String defaultAdminEmail;

    @Value("${com.andreidodu.europea-library.default-admin-password}")
    private String defaultAdminPassword;

    public void loadCache() {
        this.cacheLoader.reload();
    }

    public void addDefaultUsersAndRolesIfNecessary() {
        if (userRepository.existsByUsername(defaultAdminUsername)) {
            return;
        }
        createAdministratorUser();
    }

    private void createAdministratorUser() {
        User user = createDefaultAdminUser();
        createDefaultAdminRoles(user);
    }

    private void createDefaultAdminRoles(User user) {
        Role role = new Role();
        role.setUser(user);
        role.setName(AuthConst.ROLE_ADMINISTRATOR);
        this.roleRepository.save(role);
    }

    private User createDefaultAdminUser() {
        User user = new User();
        user.setUsername(defaultAdminUsername);
        user.setEmail(defaultAdminEmail);
        user.setPassword(bCryptPasswordEncoder.encode(defaultAdminPassword));
        user.setEnabled(true);
        user = userRepository.save(user);
        return user;
    }

}
