package com.boilerplate.domain.port.out;

import com.boilerplate.domain.model.Page;
import java.util.List;
import java.util.Optional;

public interface PageRepository {
    Page save(Page page);

    Optional<Page> findBySlug(String slug);

    List<Page> findAll();

    void deleteById(Long id);

    Optional<Page> findById(Long id);
}
