package com.boilerplate.domain.port.out;

import com.boilerplate.domain.model.PageData;
import java.util.List;
import java.util.Optional;

public interface PageDataRepository {
    PageData save(PageData pageData);

    List<PageData> findAllByPageId(Long pageId);

    Optional<PageData> findById(Long id);

    void deleteById(Long id);
}
