package ac.th.fearfreeanimals.service;

import ac.th.fearfreeanimals.entity.Coins;
import ac.th.fearfreeanimals.entity.User;
import ac.th.fearfreeanimals.repository.CoinsRepository;
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

class CoinServiceTest {

    @InjectMocks
    private CoinsService coinService; // Service ที่ต้องการทดสอบ

    @Mock
    private CoinsRepository coinRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // เตรียม Mock Object
    }

    @Test
    void addCoins_shouldIncreaseBalanceSuccessfully() {
        // Arrange
        Long userId = 1L;
        int amountToAdd = 100;

        User user = new User();
        user.setId(userId);

        Coins coin = new Coins();
        coin.setBalance(200);
        coin.setUser(user);

        when(coinRepository.findByUserId(userId)).thenReturn(Optional.of(coin));
        when(coinRepository.save(any(Coins.class))).thenReturn(coin);

        // Act
        Coins updatedCoins = coinService.addCoins(userId, amountToAdd);

        // Assert
        assertNotNull(updatedCoins);
        assertEquals(300, updatedCoins.getBalance());
        verify(coinRepository, times(1)).save(any(Coins.class));
    }

    @Test
void addCoins_shouldThrowExceptionWhenUserNotFound() {
    // Arrange
    Long userId = 1L;
    int amountToAdd = 100;

    when(coinRepository.findByUserId(userId)).thenReturn(Optional.empty());

    // Act & Assert
    Exception exception = assertThrows(RuntimeException.class, () -> {
        coinService.addCoins(userId, amountToAdd);
    });

    // Adjusted message to match service
    assertEquals("Coins not found for userId " + userId, exception.getMessage());
}


    @Test
    void deductCoins_shouldReduceBalanceSuccessfully() {
        // Arrange
        Long userId = 1L;
        int amountToDeduct = 50;

        User user = new User();
        user.setId(userId);

        Coins coin = new Coins();
        coin.setBalance(200);
        coin.setUser(user);

        when(coinRepository.findByUserId(userId)).thenReturn(Optional.of(coin));
        when(coinRepository.save(any(Coins.class))).thenReturn(coin);

        // Act
        Coins updatedCoins = coinService.subtractCoins(userId, amountToDeduct);

        // Assert
        assertNotNull(updatedCoins);
        assertEquals(150, updatedCoins.getBalance());
        verify(coinRepository, times(1)).save(any(Coins.class));
    }

    @Test
    void deductCoins_shouldThrowExceptionWhenInsufficientBalance() {
        // Arrange
        Long userId = 1L;
        int amountToDeduct = 250;

        User user = new User();
        user.setId(userId);

        Coins coin = new Coins();
        coin.setBalance(200);
        coin.setUser(user);

        when(coinRepository.findByUserId(userId)).thenReturn(Optional.of(coin));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            coinService.subtractCoins(userId, amountToDeduct);
        });

        assertEquals("Insufficient balance to deduct", exception.getMessage());
    }
}
