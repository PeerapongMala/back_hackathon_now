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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void createPatientByDoctor_shouldCreatePatientSuccessfully() {
        // Arrange
        String username = "test_patient";
        String password = "password123";
        Long doctorId = 1L;

        Role patientRole = new Role();
        patientRole.setName("PATIENT");

        Role doctorRole = new Role();
        doctorRole.setName("DOCTOR");

        User doctor = new User();
        doctor.setId(doctorId);
        doctor.setRole(doctorRole);

        User newPatient = new User(username, password, patientRole);

        when(roleRepository.findByName("PATIENT")).thenReturn(Optional.of(patientRole));
        when(userRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(userRepository.countByRoleName("PATIENT")).thenReturn(0L);
        when(userRepository.save(any(User.class))).thenReturn(newPatient);

        // Act
        User createdPatient = userService.createPatientByDoctor(username, password, doctorId);

        // Assert
        assertNotNull(createdPatient);
        assertEquals("PATIENT", createdPatient.getRole().getName());
        assertEquals(username, createdPatient.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createPatientByDoctor_shouldThrowExceptionWhenDoctorNotFound() {
        // Arrange
        Long doctorId = 1L;
        String username = "test_patient";
        String password = "password123";

        when(roleRepository.findByName("PATIENT")).thenReturn(Optional.of(new Role()));
        when(userRepository.findById(doctorId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.createPatientByDoctor(username, password, doctorId);
        });

        assertEquals("Doctor not found with ID: " + doctorId, exception.getMessage());
    }

    @Test
    void createPatientByDoctor_shouldThrowExceptionWhenDoctorRoleInvalid() {
        // Arrange
        Long doctorId = 1L;
        String username = "test_patient";
        String password = "password123";

        Role invalidRole = new Role();
        invalidRole.setName("ADMIN");

        User invalidDoctor = new User();
        invalidDoctor.setId(doctorId);
        invalidDoctor.setRole(invalidRole);

        when(roleRepository.findByName("PATIENT")).thenReturn(Optional.of(new Role()));
        when(userRepository.findById(doctorId)).thenReturn(Optional.of(invalidDoctor));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.createPatientByDoctor(username, password, doctorId);
        });

        assertEquals("User with ID: " + doctorId + " is not a doctor", exception.getMessage());
    }
    @Test
void createGeneralUser_shouldCreateUserSuccessfully() {
    // Arrange
    String username = "test_general";
    String password = "password123";

    Role generalRole = new Role();
    generalRole.setName("GENERAL");

    User generalUser = new User(username, password, generalRole);

    when(roleRepository.findByName("GENERAL")).thenReturn(Optional.of(generalRole));
    when(userRepository.save(any(User.class))).thenReturn(generalUser);

    // Act
    User createdGeneralUser = userService.createGeneralUser(username, password);

    // Assert
    assertNotNull(createdGeneralUser);
    assertEquals("GENERAL", createdGeneralUser.getRole().getName());
    assertEquals(username, createdGeneralUser.getUsername());
    verify(userRepository, times(1)).save(any(User.class));
}

}
