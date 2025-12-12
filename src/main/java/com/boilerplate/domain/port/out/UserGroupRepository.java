package com.boilerplate.domain.port.out;

import com.boilerplate.domain.model.UserGroup;
import java.util.List;
import java.util.Optional;

public interface UserGroupRepository {
    UserGroup save(UserGroup userGroup);

    Optional<UserGroup> findById(Long id);

    Optional<UserGroup> findByName(String name);

    List<UserGroup> findAll();

    void deleteById(Long id);
}
