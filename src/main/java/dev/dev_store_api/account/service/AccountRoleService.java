package dev.dev_store_api.account.service;

import dev.dev_store_api.common.util.GenericMapper;
import dev.dev_store_api.common.exception.NotFoundException;
import dev.dev_store_api.account.model.Account;
import dev.dev_store_api.account.model.AccountRole;
import dev.dev_store_api.account.model.Role;
import dev.dev_store_api.common.model.type.EMessage;
import dev.dev_store_api.common.model.type.ERole;
import dev.dev_store_api.account.repository.AccountRoleRepository;
import dev.dev_store_api.account.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountRoleService {
    private final AccountRoleRepository accountRoleRepository;
    private final RoleRepository roleRepository;

    public AccountRoleService(AccountRoleRepository accountRoleRepository, RoleRepository roleRepository, GenericMapper genericMapper) {
        this.accountRoleRepository = accountRoleRepository;
        this.roleRepository = roleRepository;
    }

    public AccountRole create (Account account, ERole roleName) {
        Role role = roleRepository.findByName(roleName.name())
                .orElseThrow(() -> new NotFoundException(EMessage.NOT_FOUND.format("role")));
        return accountRoleRepository.save(new AccountRole(account, role));
    }

}
