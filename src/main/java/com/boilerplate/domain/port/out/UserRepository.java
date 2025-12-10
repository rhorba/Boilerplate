package com.boilerplate.domain.port.out;

import java.util.Optional;
import com.boilerplate.domain.model.User;

public interface UserRepository {
    Optional<User> findByEmail(String email);

    User save(User user);

    boolean existsByEmail(String email);

    java.util.List<User> findAll();

    Optional<User> findById(Long id);

    void deleteById(Long id);
}
