package com.boilerplate.application.service;

import com.boilerplate.domain.model.Role;
import com.boilerplate.domain.port.out.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    void createRole_ShouldSaveRole() {
        Role role = Role.builder().name("USER").build();
        when(roleRepository.save(role)).thenReturn(role);

        Role result = roleService.createRole(role);

        assertEquals(role, result);
        verify(roleRepository).save(role);
    }

    @Test
    void getAllRoles_ShouldReturnList() {
        when(roleRepository.findAll()).thenReturn(List.of(Role.builder().build()));
        List<Role> result = roleService.getAllRoles();
        assertEquals(1, result.size());
    }

    @Test
    void deleteRole_ShouldDelete() {
        Long id = 1L;
        roleService.deleteRole(id);
        verify(roleRepository).deleteById(id);
    }
}
