package com.boilerplate.infrastructure.adapter.out.persistence.repository;

import com.boilerplate.infrastructure.adapter.out.persistence.entity.UserGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataUserGroupRepository extends JpaRepository<UserGroupEntity, Long> {
    Optional<UserGroupEntity> findByName(String name);
}
