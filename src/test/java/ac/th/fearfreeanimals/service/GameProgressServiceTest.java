package ac.th.fearfreeanimals.service;

import ac.th.fearfreeanimals.entity.GameProgress;
import ac.th.fearfreeanimals.entity.User;
import ac.th.fearfreeanimals.entity.Role;
import ac.th.fearfreeanimals.repository.GameProgressRepository;
import ac.th.fearfreeanimals.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameProgressServiceTest {

    @InjectMocks
    private GameProgressService gameProgressService;

    @Mock
    private GameProgressRepository gameProgressRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createGameProgress_shouldCreateGameProgressSuccessfully() {
        // Arrange
        Long userId = 1L;
        GameProgress newProgress = new GameProgress();

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(gameProgressRepository.save(any(GameProgress.class))).thenReturn(newProgress);

        // Act
        GameProgress result = gameProgressService.createGameProgress(userId, newProgress);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).findById(userId);
        verify(gameProgressRepository, times(1)).save(newProgress);
    }

    @Test
    void updateGameProgress_shouldUpdateGameProgressSuccessfully() {
        // Arrange
        Long userId = 1L;
        GameProgress gameProgressDetails = new GameProgress();
        gameProgressDetails.setCurrentLevel(5);
        gameProgressDetails.setAnimalType("Dog");
        gameProgressDetails.setCompleted(true);
        gameProgressDetails.setDescription("New description");

        GameProgress existingProgress = new GameProgress();
        when(gameProgressRepository.findByUserId(userId)).thenReturn(Optional.of(existingProgress));
        when(gameProgressRepository.save(any(GameProgress.class))).thenReturn(existingProgress);

        // Act
        GameProgress result = gameProgressService.updateGameProgress(userId, gameProgressDetails);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getCurrentLevel());
        assertEquals("Dog", result.getAnimalType());
        assertTrue(result.getCompleted());
        assertEquals("New description", result.getDescription());
        verify(gameProgressRepository, times(1)).save(existingProgress);
    }

    @Test
    void nextLevel_shouldIncreaseLevelSuccessfully() {
        // Arrange
        Long userId = 1L;
        GameProgress progress = new GameProgress();
        progress.setCurrentLevel(1);

        when(gameProgressRepository.findByUserId(userId)).thenReturn(Optional.of(progress));
        when(gameProgressRepository.save(any(GameProgress.class))).thenReturn(progress);

        // Act
        GameProgress result = gameProgressService.nextLevel(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getCurrentLevel());
        verify(gameProgressRepository, times(1)).save(progress);
    }

    @Test
    void updateSymptomNotes_shouldUpdateNotesSuccessfully() {
        // Arrange
        Long userId = 1L;
        String animal = "Dog";
        int level = 3;
        String text = "Note for level 3";

        GameProgress progress = new GameProgress();
        progress.setSymptomNotes(new HashMap<>());

        when(gameProgressRepository.findByUserIdAndAnimalType(userId, animal)).thenReturn(Optional.of(progress));
        when(gameProgressRepository.save(any(GameProgress.class))).thenReturn(progress);

        // Act
        GameProgress result = gameProgressService.updateSymptomNotes(userId, animal, level, text);

        // Assert
        assertNotNull(result);
        assertEquals("Note for level 3", result.getSymptomNotes().get(level));
        verify(gameProgressRepository, times(1)).save(progress);
    }

    @Test
    void unlockNextLevel_shouldUnlockNextLevelAndAddCoins() {
        // Arrange
        Long userId = 1L;

        User user = new User();
        user.setRole(new Role());
        user.getRole().setName("ADMIN");
        user.setCoins(5);

        GameProgress progress = new GameProgress();
        progress.setCurrentLevel(9);
        progress.setUser(user);

        when(gameProgressRepository.findByUserId(userId)).thenReturn(Optional.of(progress));
        when(gameProgressRepository.save(any(GameProgress.class))).thenReturn(progress);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        GameProgress result = gameProgressService.unlockNextLevel(userId);

        // Assert
        assertNotNull(result);
        assertEquals(10, result.getCurrentLevel());
        assertEquals(6, user.getCoins()); // Coins incremented
        verify(gameProgressRepository, times(1)).save(progress);
        verify(userRepository, times(1)).save(user);
    }
}
