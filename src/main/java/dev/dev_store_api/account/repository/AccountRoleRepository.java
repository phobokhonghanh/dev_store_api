package dev.dev_store_api.account.repository;

import dev.dev_store_api.account.model.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRoleRepository extends JpaRepository<AccountRole, Long> {
}