package ac.th.fearfreeanimals.controller;

import ac.th.fearfreeanimals.entity.GameProgress;
import ac.th.fearfreeanimals.service.GameProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/game-progress")
public class GameProgressController {

    @Autowired
    private GameProgressService gameProgressService;
    
    // Create game progress
    @PostMapping("/{userId}")
    public ResponseEntity<GameProgress> createGameProgress(
            @PathVariable Long userId,
            @RequestBody GameProgress newProgress) {
        GameProgress createdProgress = gameProgressService.createGameProgress(userId, newProgress);
        return ResponseEntity.ok(createdProgress);
    }

    // Move to the next level
    @PutMapping("/next-level/{userId}")
    public ResponseEntity<GameProgress> nextLevel(@PathVariable Long userId) {
        GameProgress updatedProgress = gameProgressService.nextLevel(userId);
        return ResponseEntity.ok(updatedProgress);
    }
    

    @PutMapping("/progress/general/{userId}")
    public ResponseEntity<GameProgress> updateGameProgressForGeneralUser(
            @PathVariable Long userId,
            @RequestParam String animalType,
            @RequestParam int level) {
        System.out.println("UserId: " + userId);
        System.out.println("AnimalType: " + animalType);
        System.out.println("Level: " + level);
    
        GameProgress progress = gameProgressService.updateGameProgressForGeneralUser(userId, animalType, level);
        return ResponseEntity.ok(progress);
    }
    
// ดึงความคืบหน้าเกม
    @GetMapping("/{userId}")
    public ResponseEntity<GameProgress> getGameProgress(@PathVariable Long userId) {
        GameProgress progress = gameProgressService.getOrCreateGameProgress(userId, "default");
        return ResponseEntity.ok(progress);
    }

    // ปลดล็อกเลเวลถัดไป
    @PutMapping("/{userId}/next-level")
    public ResponseEntity<GameProgress> unlockNextLevel(@PathVariable Long userId) {
        GameProgress progress = gameProgressService.unlockNextLevel(userId);
        return ResponseEntity.ok(progress);
    }

    @PutMapping("/{userId}/update-symptom")
public ResponseEntity<?> updateSymptomNotes(
        @PathVariable Long userId,
        @RequestBody Map<String, Object> payload) {
    // ตรวจสอบว่า payload มีคีย์ที่จำเป็นหรือไม่
    if (!payload.containsKey("animalType") || !payload.containsKey("level") || !payload.containsKey("notes")) {
        return ResponseEntity.badRequest().body("Missing required fields: animalType, level, or notes");
    }

    // ดึงข้อมูลจาก payload
    String animalType = (String) payload.get("animalType");
    Integer level = (Integer) payload.get("level"); // ใช้ Integer เพื่อรองรับ null
    String notes = (String) payload.get("notes");

    System.out.println("Animal Type: " + animalType);
    System.out.println("Level: " + level);
    System.out.println("Notes: " + notes);

    // ตรวจสอบว่าค่าที่จำเป็นไม่เป็น null หรือว่าง
    if (animalType == null || animalType.trim().isEmpty()) {
        return ResponseEntity.badRequest().body("Animal type cannot be null or empty.");
    }
    if (level == null || level < 1) {
        return ResponseEntity.badRequest().body("Level must be greater than or equal to 1.");
    }
    if (notes == null || notes.trim().isEmpty()) {
        return ResponseEntity.badRequest().body("Notes cannot be null or empty.");
    }

    // อัปเดตหรือสร้างความคืบหน้า
    GameProgress progress = gameProgressService.createOrUpdateProgress(userId, animalType, level, notes);

    // ส่งข้อมูลกลับ
    return ResponseEntity.ok(progress);
}

    // Get game progress by userId and animalType
    @GetMapping("/{userId}/{animal}")
    public ResponseEntity<GameProgress> getGameProgress(
            @PathVariable Long userId,
            @PathVariable String animal) {
        GameProgress progress = gameProgressService.getGameProgress(userId, animal);
        return ResponseEntity.ok(progress);
    }

    @PutMapping("/game-progress/{userId}/update-level")
    public ResponseEntity<?> updateLevel(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> payload) {
        String animal = (String) payload.get("animal");
        int level = (int) payload.get("level");
    
        GameProgress progress = gameProgressService.updateGameProgress(userId, animal, level);
        return ResponseEntity.ok(progress);
    }
   
}
