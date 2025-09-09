package dev.dev_store_api.repository;

import dev.dev_store_api.model.ThirdParty;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository cho ThirdParty
public interface ThirdPartyRepository extends JpaRepository<ThirdParty, Long> {
}