package dev.dev_store_api.account.repository;

import dev.dev_store_api.account.model.Account;
import dev.dev_store_api.account.model.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface AccountRoleRepository extends JpaRepository<AccountRole, Long> {
    List<AccountRole> findByAccount(Account account);
}