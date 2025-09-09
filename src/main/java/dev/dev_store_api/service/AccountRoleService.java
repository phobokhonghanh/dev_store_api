package dev.dev_store_api.service;

import dev.dev_store_api.libs.utils.GenericMapper;
import dev.dev_store_api.libs.utils.exception.NotFoundException;
import dev.dev_store_api.model.Account;
import dev.dev_store_api.model.AccountRole;
import dev.dev_store_api.model.Role;
import dev.dev_store_api.repository.AccountRoleRepository;
import dev.dev_store_api.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountRoleService {
    private final AccountRoleRepository accountRoleRepository;
    private final RoleRepository roleRepository;

    public AccountRoleService(AccountRoleRepository accountRoleRepository, RoleRepository roleRepository, GenericMapper genericMapper) {
        this.accountRoleRepository = accountRoleRepository;
        this.roleRepository = roleRepository;
    }

    public AccountRole create (Account account, String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("Role not found: " + roleName));

        AccountRole accountRole = new AccountRole(account, role);
        accountRole = accountRoleRepository.save(accountRole);
        return accountRole;
    }

}
