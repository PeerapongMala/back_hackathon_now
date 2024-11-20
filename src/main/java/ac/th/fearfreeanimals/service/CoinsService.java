package ac.th.fearfreeanimals.service;

import ac.th.fearfreeanimals.entity.Coins;
import ac.th.fearfreeanimals.entity.User;
import ac.th.fearfreeanimals.repository.CoinsRepository;
import ac.th.fearfreeanimals.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class CoinsService {

    private final CoinsRepository coinsRepository;
    private final UserRepository userRepository;

    @Autowired
    public CoinsService(CoinsRepository coinsRepository, UserRepository userRepository) {
        this.coinsRepository = coinsRepository;
        this.userRepository = userRepository;
    }
    // ฟังก์ชันดึงข้อมูลเหรียญทั้งหมด
    public List<Coins> getAllCoins() {
        return coinsRepository.findAll();
    }
    // ฟังก์ชันสร้างเหรียญใหม่
    public Coins createCoins(Coins coins) {
        return coinsRepository.save(coins);
    }
    // ฟังก์ชันดึงเหรียญตาม ID
    public Coins getCoinsById(Long id) {
        Optional<Coins> coins = coinsRepository.findById(id);
        return coins.orElseThrow(() -> new RuntimeException("Coins not found with id " + id));
    }
    // ฟังก์ชันอัปเดตข้อมูลเหรียญ
    public Coins updateCoins(Long id, Coins coinsDetails) {
        Coins coins = getCoinsById(id); // ฟังก์ชันที่ดึงเหรียญตาม id
        coins.setBalance(coinsDetails.getBalance());
        coins.setLastUpdated(coinsDetails.getLastUpdated());
        return coinsRepository.save(coins);
    }
    // ฟังก์ชันลบเหรียญ
    public void deleteCoins(Long id) {
        Coins coins = getCoinsById(id); // ฟังก์ชันที่ดึงเหรียญตาม id
        coinsRepository.delete(coins);
    }

    


    // ฟังก์ชันเพิ่มเหรียญให้กับผู้ใช้
    public void addCoins(Long userId, int coinsToAdd) {
        // ค้นหาเหรียญของผู้ใช้
        Coins coins = coinsRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Coins not found for userId " + userId));

        // เพิ่มเหรียญ
        coins.setBalance(coins.getBalance() + coinsToAdd);

        // บันทึกข้อมูลที่อัปเดต
        coinsRepository.save(coins);
    }

    // ลดจำนวนเหรียญของผู้ใช้
    public void subtractCoins(Long userId, int coinsToSubtract) {
        // ค้นหาผู้ใช้
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));
        // ค้นหาเหรียญของผู้ใช้
        Coins coins = coinsRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Coins not found for userId " + userId));

        // ตรวจสอบว่าเหรียญที่ผู้ใช้มีเพียงพอหรือไม่
        if (coins.getBalance() < coinsToSubtract) {
            throw new RuntimeException("Not enough coins to redeem reward.");
        }
        
        // ลดเหรียญ
        coins.setBalance(coins.getBalance() - coinsToSubtract);
        
        userRepository.save(user);

        // บันทึกข้อมูลเหรียญที่อัปเดต
        coinsRepository.save(coins);
        
    }
}
