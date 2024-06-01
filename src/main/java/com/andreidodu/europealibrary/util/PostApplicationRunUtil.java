package com.andreidodu.europealibrary.util;

import com.andreidodu.europealibrary.constants.AuthConst;
import com.andreidodu.europealibrary.model.security.Authority;
import com.andreidodu.europealibrary.model.security.User;
import com.andreidodu.europealibrary.repository.security.RoleRepository;
import com.andreidodu.europealibrary.repository.security.UserRepository;
import com.andreidodu.europealibrary.service.ApplicationSettingsService;
import com.andreidodu.europealibrary.service.CacheLoaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostApplicationRunUtil {
    private final ApplicationSettingsService applicationSettingsService;
    private final CacheLoaderService cacheLoaderService;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Value("${com.andreidodu.europea-library.default-admin-username}")
    private String defaultAdminUsername;

    @Value("${com.andreidodu.europea-library.default-admin-email}")
    private String defaultAdminEmail;

    @Value("${com.andreidodu.europea-library.default-admin-password}")
    private String defaultAdminPassword;

    public void loadCache() {
        this.cacheLoaderService.reload();
    }

    public void addDefaultUsersAndRolesIfNecessary() {
        if (userRepository.existsByUsername(defaultAdminUsername)) {
            return;
        }
        createAdministratorUser();
    }

    public void createDefaultApplicationSettings() {
        this.applicationSettingsService.loadOrCreateDefaultApplicationSettings();

    }

    private void createAdministratorUser() {
        User user = createDefaultAdminUser();
        createDefaultAdminRoles(user);
    }

    private void createDefaultAdminRoles(User user) {
        Authority authority = new Authority();
        authority.setUser(user);
        authority.setName(AuthConst.AUTHORITY_ADMINISTRATOR);
        this.roleRepository.save(authority);
    }

    private User createDefaultAdminUser() {
        User user = new User();
        user.setUsername(defaultAdminUsername);
        user.setEmail(defaultAdminEmail);
        user.setPassword(bCryptPasswordEncoder.encode(defaultAdminPassword));
        user.setEnabled(true);
        user.setConsensus1Flag(true);
        user.setConsensus2Flag(true);
        user.setConsensus3Flag(false);
        user = userRepository.save(user);
        return user;
    }

}
