package com.example.generator;

import com.example.entity.DoctorEntity;
import com.example.entity.Sex;
import com.example.utils.RandomUtils;

import java.util.SplittableRandom;

public class DoctorGenerator {

    private static final String[] SPECIALTIES = {
        "Cardiology", "Neurology", "Orthopedics", "Dermatology", "Pediatrics",
        "Oncology", "Radiology", "Psychiatry", "Endocrinology", "Gastroenterology",
        "Pulmonology", "Nephrology", "Rheumatology", "Ophthalmology", "Urology",
        "General Practice", "Emergency Medicine", "Anesthesiology", "Hematology"
    };

    private static final String[] HOSPITALS = {
        "Saint Mary's Hospital", "City General Hospital", "University Medical Center",
        "Riverside Clinic", "Northside Health Institute", "Central Hospital",
        "Eastview Medical Center", "Lakewood Hospital", "Sunrise Health Complex",
        "Greenfield Medical Institute"
    };

    private final SplittableRandom rng;

    public DoctorGenerator(long seed) {
        this.rng = new SplittableRandom(seed);
    }

    public DoctorEntity generate() {
        DoctorEntity doctor = new DoctorEntity();

        Sex sex = RandomUtils.pick(Sex.values(), rng);
        doctor.setSex(sex);
        doctor.setName(NameGenerator.randomName(rng));
        doctor.setSpecialty(RandomUtils.pick(SPECIALTIES, rng));
        doctor.setYearsOfExperience(rng.nextInt(1, 40));
        doctor.setHospital(RandomUtils.pick(HOSPITALS, rng));

        return doctor;
    }
}
