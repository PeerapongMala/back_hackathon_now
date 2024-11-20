package ac.th.fearfreeanimals.service;

import ac.th.fearfreeanimals.entity.GameProgress;
import ac.th.fearfreeanimals.entity.Role;
import ac.th.fearfreeanimals.entity.User;
import ac.th.fearfreeanimals.repository.GameProgressRepository;
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

class DoctorServicetest {

    @InjectMocks
    private DoctorService doctorService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private GameProgressRepository gameProgressRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addPatient_shouldAddPatientSuccessfully() {
        // Arrange
        User patient = new User();
        patient.setUsername("patient_user");
        patient.setPassword("patient_pass");

        Role patientRole = new Role();
        patientRole.setName("PATIENT");

        User createdPatient = new User();
        createdPatient.setId(1L);
        createdPatient.setUsername("patient_user");
        createdPatient.setRole(patientRole);

        GameProgress gameProgress = new GameProgress(createdPatient, null);

        when(roleRepository.findByName("PATIENT")).thenReturn(Optional.of(patientRole));
        when(userRepository.save(any(User.class))).thenReturn(createdPatient);
        when(gameProgressRepository.save(any(GameProgress.class))).thenReturn(gameProgress);

        // Act
        User result = doctorService.addPatient(patient);

        // Assert
        assertNotNull(result);
        assertEquals("PATIENT", result.getRole().getName());
        assertEquals("FFANM001", result.getAccessCode());
        verify(userRepository, times(2)).save(any(User.class)); // Once for creation and once for updating the access code
        verify(gameProgressRepository, times(1)).save(any(GameProgress.class));
    }

    @Test
    void addPatient_shouldThrowExceptionWhenRoleNotFound() {
        // Arrange
        User patient = new User();
        patient.setUsername("patient_user");

        when(roleRepository.findByName("PATIENT")).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            doctorService.addPatient(patient);
        });

        assertEquals("Role PATIENT not found", exception.getMessage());
    }

    @Test
    void setAnimalType_shouldSetAnimalTypeSuccessfully() {
        // Arrange
        Long userId = 1L;
        String animalType = "Cat";

        GameProgress gameProgress = new GameProgress();
        gameProgress.setAnimalType(null);

        when(gameProgressRepository.findByUserId(userId)).thenReturn(Optional.of(gameProgress));
        when(gameProgressRepository.save(any(GameProgress.class))).thenReturn(gameProgress);

        // Act
        GameProgress result = doctorService.setAnimalType(userId, animalType);

        // Assert
        assertNotNull(result);
        assertEquals("Cat", result.getAnimalType());
        verify(gameProgressRepository, times(1)).save(any(GameProgress.class));
    }

    @Test
    void setAnimalType_shouldThrowExceptionWhenGameProgressNotFound() {
        // Arrange
        Long userId = 1L;
        String animalType = "Dog";

        when(gameProgressRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            doctorService.setAnimalType(userId, animalType);
        });

        assertEquals("GameProgress not found for user ID " + userId, exception.getMessage());
    }

    @Test
    void startGame_shouldStartGameSuccessfully() {
        // Arrange
        Long userId = 1L;

        GameProgress gameProgress = new GameProgress();
        gameProgress.setCurrentLevel(0);
        gameProgress.setCompleted(true);

        when(gameProgressRepository.findByUserId(userId)).thenReturn(Optional.of(gameProgress));
        when(gameProgressRepository.save(any(GameProgress.class))).thenReturn(gameProgress);

        // Act
        GameProgress result = doctorService.startGame(userId);

        // Assert
    assertNotNull(result);
    assertEquals(1, result.getCurrentLevel());
    assertNotNull(result.getCompleted(), "Completed field should not be null");
    assertFalse(result.getCompleted());
    verify(gameProgressRepository, times(1)).save(any(GameProgress.class));
    }

    @Test
    void startGame_shouldThrowExceptionWhenGameProgressNotFound() {
        // Arrange
        Long userId = 1L;

        when(gameProgressRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            doctorService.startGame(userId);
        });

        assertEquals("GameProgress not found for user ID " + userId, exception.getMessage());
    }
}
