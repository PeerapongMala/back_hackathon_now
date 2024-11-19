package ac.th.fearfreeanimals.service;

import ac.th.fearfreeanimals.entity.Role;
import ac.th.fearfreeanimals.entity.User;
import ac.th.fearfreeanimals.repository.RoleRepository;
import ac.th.fearfreeanimals.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Adminservice {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    // ฟังก์ชันสำหรับสร้าง Doctor (คงเดิม)
    public User createDoctor(String username, String password) {
        Role doctorRole = roleRepository.findByName("DOCTOR")
                .orElseThrow(() -> new RuntimeException("Role DOCTOR not found"));

        User doctor = new User();
        doctor.setUsername(username);
        doctor.setPassword(password); // ควรเข้ารหัสด้วย BCrypt
        doctor.setRole(doctorRole);
        doctor.setIsDoctor(true);

        return userRepository.save(doctor);
    }

    // ฟังก์ชันสำหรับสร้าง Admin
    public User createAdmin(String username, String password) {
        // ค้นหา Role "ADMIN"
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Role ADMIN not found"));

        // สร้างบัญชี Admin ใหม่
        User admin = new User();
        admin.setUsername(username);
        admin.setPassword(password); // ควรเข้ารหัสด้วย BCrypt
        admin.setRole(adminRole);

        return userRepository.save(admin);
    }

    // ฟังก์ชันสำหรับลบ Admin
    public void deleteAdmin(Long adminId) {
        // ตรวจสอบว่ามี Admin อยู่หรือไม่
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + adminId));

        userRepository.delete(admin);
    }
}
