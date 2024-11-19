package ac.th.fearfreeanimals.service;

import ac.th.fearfreeanimals.entity.*;
import ac.th.fearfreeanimals.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public User createPatientByDoctor(String username, String password, Long doctorId) {
        // Find the "PATIENT" role
        Role patientRole = roleRepository.findByName("PATIENT")
                .orElseThrow(() -> new RuntimeException("Role PATIENT not found"));

        // Verify doctor
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));
        if (!"DOCTOR".equals(doctor.getRole().getName())) {
            throw new RuntimeException("User with ID: " + doctorId + " is not a doctor");
        }

        // Create new patient
        User patient = new User(username, password, patientRole);

        // Generate Access Code
        String accessCode = "FFANM" + String.format("%03d", (userRepository.countByRoleName("PATIENT") + 1));
        patient.setAccessCode(accessCode);

        // Save patient to the database
        return userRepository.save(patient);
    }public User createGeneralUser(String username, String password) {
        Role generalRole = roleRepository.findByName("GENERAL")
                .orElseThrow(() -> new RuntimeException("Role GENERAL not found"));
    
        User generalUser = new User(username, password, generalRole);
        return userRepository.save(generalUser);
    }
    
}
