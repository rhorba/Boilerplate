package com.boilerplate.domain.repository;

import com.boilerplate.domain.model.UserAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAttributeRepository extends JpaRepository<UserAttribute, Long> {

    List<UserAttribute> findAllByUserId(Long userId);

    Optional<UserAttribute> findByUserIdAndAttributeKey(Long userId, String attributeKey);

    boolean existsByUserIdAndAttributeKey(Long userId, String attributeKey);

    void deleteByUserIdAndAttributeKey(Long userId, String attributeKey);
}
