package ac.th.fearfreeanimals.service;

import ac.th.fearfreeanimals.entity.Reward;
import ac.th.fearfreeanimals.entity.RewardRedemption;
import ac.th.fearfreeanimals.entity.Role;
import ac.th.fearfreeanimals.entity.User;
import ac.th.fearfreeanimals.repository.RewardRedemptionRepository;
import ac.th.fearfreeanimals.repository.RewardRepository;
import ac.th.fearfreeanimals.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RewardRedemptionServiceTest {

    @InjectMocks
    private RewardRedemptionService rewardRedemptionService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RewardRepository rewardRepository;

    @Mock
    private RewardRedemptionRepository rewardRedemptionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void redeemReward_shouldRedeemRewardSuccessfully() {
        // Arrange
        Long userId = 1L;
        Long rewardId = 2L;

        Role adminRole = new Role();
        adminRole.setName("ADMIN");

        User user = new User();
        user.setId(userId);
        user.setCoins(100);
        user.setRole(adminRole);

        Reward reward = new Reward();
        reward.setId(rewardId);
        reward.setCoinCost(50);

        RewardRedemption redemption = new RewardRedemption(user, reward);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(rewardRepository.findById(rewardId)).thenReturn(Optional.of(reward));
        when(rewardRedemptionRepository.save(any(RewardRedemption.class))).thenReturn(redemption);

        // Act
        RewardRedemption result = rewardRedemptionService.redeemReward(userId, rewardId);

        // Assert
        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(reward, result.getReward());
        assertEquals(50, user.getCoins()); // 100 - 50
        verify(userRepository, times(1)).save(user);
        verify(rewardRedemptionRepository, times(1)).save(any(RewardRedemption.class));
    }

    @Test
    void redeemReward_shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        Long userId = 1L;
        Long rewardId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            rewardRedemptionService.redeemReward(userId, rewardId);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void redeemReward_shouldThrowExceptionWhenUserIsPatient() {
        // Arrange
        Long userId = 1L;
        Long rewardId = 2L;

        Role patientRole = new Role();
        patientRole.setName("PATIENT");

        User user = new User();
        user.setId(userId);
        user.setRole(patientRole);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            rewardRedemptionService.redeemReward(userId, rewardId);
        });

        assertEquals("Patients cannot redeem rewards.", exception.getMessage());
    }

    @Test
    void redeemReward_shouldThrowExceptionWhenRewardNotFound() {
        // Arrange
        Long userId = 1L;
        Long rewardId = 2L;

        Role adminRole = new Role();
        adminRole.setName("ADMIN");

        User user = new User();
        user.setId(userId);
        user.setRole(adminRole);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(rewardRepository.findById(rewardId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            rewardRedemptionService.redeemReward(userId, rewardId);
        });

        assertEquals("Reward not found", exception.getMessage());
    }

    @Test
    void redeemReward_shouldThrowExceptionWhenInsufficientCoins() {
        // Arrange
        Long userId = 1L;
        Long rewardId = 2L;

        Role adminRole = new Role();
        adminRole.setName("ADMIN");

        User user = new User();
        user.setId(userId);
        user.setCoins(30); // Coins less than reward cost
        user.setRole(adminRole);

        Reward reward = new Reward();
        reward.setId(rewardId);
        reward.setCoinCost(50);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(rewardRepository.findById(rewardId)).thenReturn(Optional.of(reward));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            rewardRedemptionService.redeemReward(userId, rewardId);
        });

        assertEquals("Insufficient coins.", exception.getMessage());
    }
}
