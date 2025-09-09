package dev.dev_store_api.repository;

import dev.dev_store_api.model.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRoleRepository extends JpaRepository<AccountRole, Long> {
}