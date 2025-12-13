package com.boilerplate.infrastructure.adapter.out.persistence;

import com.boilerplate.domain.model.PageData;
import com.boilerplate.infrastructure.adapter.out.persistence.entity.PageDataEntity;
import com.boilerplate.infrastructure.adapter.out.persistence.repository.SpringDataPageDataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PageDataRepositoryAdapterTest {

    @Mock
    private SpringDataPageDataRepository repository;

    @InjectMocks
    private PageDataRepositoryAdapter adapter;

    @Test
    void save_ShouldReturnPageData() {
        PageData data = PageData.builder().data("{}").build();
        PageDataEntity entity = PageDataEntity.builder().id(1L).data("{}").build();
        when(repository.save(any(PageDataEntity.class))).thenReturn(entity);

        PageData result = adapter.save(data);

        assertEquals(1L, result.getId());
        assertEquals("{}", result.getData());
    }

    @Test
    void findAllByPageId_ShouldReturnList() {
        PageDataEntity entity = PageDataEntity.builder().id(1L).build();
        when(repository.findAllByPageId(1L)).thenReturn(Collections.singletonList(entity));

        assertEquals(1, adapter.findAllByPageId(1L).size());
    }

    @Test
    void findById_ShouldReturnData() {
        PageDataEntity entity = PageDataEntity.builder().id(1L).build();
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<PageData> result = adapter.findById(1L);

        assertTrue(result.isPresent());
    }

    @Test
    void deleteById_ShouldCallRepo() {
        doNothing().when(repository).deleteById(1L);
        adapter.deleteById(1L);
        verify(repository).deleteById(1L);
    }
}
