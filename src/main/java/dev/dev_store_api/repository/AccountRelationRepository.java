package dev.dev_store_api.repository;

import dev.dev_store_api.model.AccountRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface AccountRelationRepository extends JpaRepository<AccountRelation, Long> {
    Page<AccountRelation> findAllByParentId(Long parentId, Pageable pageable);
}