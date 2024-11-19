package ac.th.fearfreeanimals.service;

import ac.th.fearfreeanimals.entity.Role;
import ac.th.fearfreeanimals.entity.User;
import ac.th.fearfreeanimals.repository.RoleRepository;
import ac.th.fearfreeanimals.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @InjectMocks
    private Adminservice adminService; // Service ที่ต้องการทดสอบ

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // เตรียม Mock Object
    }

    @Test
    void createAdmin_shouldCreateAdminSuccessfully() {
        // Arrange
        String username = "admin_user";
        String password = "admin_pass";

        Role adminRole = new Role();
        adminRole.setName("ADMIN");

        User newAdmin = new User(username, password, adminRole);

        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(adminRole));
        when(userRepository.save(any(User.class))).thenReturn(newAdmin);

        // Act
        User createdAdmin = adminService.createAdmin(username, password);

        // Assert
        assertNotNull(createdAdmin);
        assertEquals("ADMIN", createdAdmin.getRole().getName());
        assertEquals(username, createdAdmin.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createAdmin_shouldThrowExceptionWhenRoleNotFound() {
        // Arrange
        String username = "admin_user";
        String password = "admin_pass";

        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            adminService.createAdmin(username, password);
        });

        assertEquals("Role ADMIN not found", exception.getMessage());
    }

    @Test
    void deleteAdmin_shouldDeleteAdminSuccessfully() {
        // Arrange
        Long adminId = 1L;

        User admin = new User();
        admin.setId(adminId);

        when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));

        // Act
        adminService.deleteAdmin(adminId);

        // Assert
        verify(userRepository, times(1)).delete(admin);
    }

    @Test
    void deleteAdmin_shouldThrowExceptionWhenAdminNotFound() {
        // Arrange
        Long adminId = 1L;

        when(userRepository.findById(adminId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            adminService.deleteAdmin(adminId);
        });

        assertEquals("Admin not found with ID: " + adminId, exception.getMessage());
    }
}
