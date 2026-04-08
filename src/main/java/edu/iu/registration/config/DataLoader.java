package edu.iu.registration.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.iu.registration.data.entities.AppUser;
import edu.iu.registration.data.entities.Course;
import edu.iu.registration.data.entities.CourseOffering;
import edu.iu.registration.data.entities.Department;
import edu.iu.registration.data.entities.Major;
import edu.iu.registration.data.entities.Minor;
import edu.iu.registration.data.entities.Prerequisite;
import edu.iu.registration.data.entities.Specialization;
import edu.iu.registration.data.entities.Term;
import edu.iu.registration.data.repositories.AppUserRepository;
import edu.iu.registration.data.repositories.CourseOfferingRepository;
import edu.iu.registration.data.repositories.CourseRepository;
import edu.iu.registration.data.repositories.DepartmentRepository;
import edu.iu.registration.data.repositories.MajorRepository;
import edu.iu.registration.data.repositories.MinorRepository;
import edu.iu.registration.data.repositories.PrerequisiteRepository;
import edu.iu.registration.data.repositories.SpecializationRepository;
import edu.iu.registration.data.repositories.TermRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(
            DepartmentRepository departmentRepository,
            MajorRepository majorRepository,
            MinorRepository minorRepository,
            SpecializationRepository specializationRepository,
            TermRepository termRepository,
            CourseRepository courseRepository,
            PrerequisiteRepository prerequisiteRepository,
            AppUserRepository appUserRepository,
            CourseOfferingRepository courseOfferingRepository
    ) {
        return args -> {
            if (departmentRepository.count() > 0) {
                return;
            }

            Department cs = departmentRepository.save(new Department("Computer Science"));
            Department math = departmentRepository.save(new Department("Mathematics"));

            Major csMajor = majorRepository.save(new Major("B.S. Computer Science", cs));
            Major mathMajor = majorRepository.save(new Major("B.S. Mathematics", math));

            Minor mathMinor = minorRepository.save(new Minor("Mathematics Minor", math));

            Specialization softwareEng =
                    specializationRepository.save(new Specialization("Software Engineering", csMajor));

            Term fall2026 = termRepository.save(new Term("Fall 2026", true));
            termRepository.save(new Term("Spring 2027", false));

            Course cs101 = courseRepository.save(new Course("CSCI-A101", "Intro to Programming", 3, cs, false));
            Course cs201 = courseRepository.save(new Course("CSCI-A201", "Data Structures", 3, cs, false));
            Course cs301 = courseRepository.save(new Course("CSCI-A301", "Algorithms", 3, cs, false));
            Course cs310 = courseRepository.save(new Course("CSCI-A310", "Software Engineering", 3, cs, false));
            Course math101 = courseRepository.save(new Course("MATH-M118", "Finite Mathematics", 3, math, false));
            Course math221 = courseRepository.save(new Course("MATH-M212", "Calculus II", 4, math, false));

            prerequisiteRepository.save(new Prerequisite(cs201, cs101));
            prerequisiteRepository.save(new Prerequisite(cs301, cs201));
            prerequisiteRepository.save(new Prerequisite(cs310, cs201));
            prerequisiteRepository.save(new Prerequisite(math221, math101));

            courseOfferingRepository.save(new CourseOffering(cs101, fall2026, 30, 20));
            courseOfferingRepository.save(new CourseOffering(cs201, fall2026, 25, 25));
            courseOfferingRepository.save(new CourseOffering(cs301, fall2026, 20, 10));
            courseOfferingRepository.save(new CourseOffering(cs310, fall2026, 20, 8));
            courseOfferingRepository.save(new CourseOffering(math101, fall2026, 40, 35));
            courseOfferingRepository.save(new CourseOffering(math221, fall2026, 35, 35));


            appUserRepository.save(new AppUser(
                    "user",
                    "userpass",
                    "STUDENT",
                    "Sophomore",
                    csMajor,
                    mathMinor,
                    softwareEng
            ));

            appUserRepository.save(new AppUser(
                    "admin",
                    "adminpass",
                    "ADMIN",
                    "Senior",
                    csMajor,
                    null,
                    null
            ));
        };
    }
}