package com.boilerplate.domain.port.out;

import com.boilerplate.domain.model.Role;
import java.util.List;
import java.util.Optional;

public interface RoleRepository {
    Role save(Role role);

    Optional<Role> findByName(String name);

    List<Role> findAll();

    void deleteById(Long id);

    Optional<Role> findById(Long id);
}
