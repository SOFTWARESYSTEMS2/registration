package edu.iu.registration.config;

import java.util.List;

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
            majorRepository.save(new Major("B.S. Mathematics", math));

            Minor mathMinor = minorRepository.save(new Minor("Mathematics Minor", math));

            Specialization softwareEng =
                    specializationRepository.save(new Specialization("Software Engineering", csMajor));

            Term fall2025 = termRepository.save(new Term("Fall 2025", false));
            Term spring2026 = termRepository.save(new Term("Spring 2026", false));
            Term fall2026 = termRepository.save(new Term("Fall 2026", true));
            Term spring2027 = termRepository.save(new Term("Spring 2027", false));

            List<Term> terms = List.of(fall2025, spring2026, fall2026, spring2027);

            Course cs101 = courseRepository.save(new Course("CSCI-A101", "Intro to Programming", 3, cs, false));
            Course cs201 = courseRepository.save(new Course("CSCI-A201", "Data Structures", 3, cs, false));
            Course cs301 = courseRepository.save(new Course("CSCI-A301", "Algorithms", 3, cs, false));
            Course cs310 = courseRepository.save(new Course("CSCI-A310", "Software Engineering", 3, cs, false));
            Course math101 = courseRepository.save(new Course("MATH-M118", "Finite Mathematics", 3, math, false));
            Course math221 = courseRepository.save(new Course("MATH-M212", "Calculus II", 4, math, false));

            List<Course> courses = List.of(cs101, cs201, cs301, cs310, math101, math221);

            prerequisiteRepository.save(new Prerequisite(cs201, cs101));
            prerequisiteRepository.save(new Prerequisite(cs301, cs201));
            prerequisiteRepository.save(new Prerequisite(cs310, cs201));
            prerequisiteRepository.save(new Prerequisite(math221, math101));

            for (Term term : terms) {
                for (Course course : courses) {
                    int capacity = 30;
                    int enrolledCount = 0;

                    if (course.getCode().equals("CSCI-A201")) {
                        capacity = 25;
                    } else if (course.getCode().equals("CSCI-A301") || course.getCode().equals("CSCI-A310")) {
                        capacity = 20;
                    } else if (course.getCode().equals("MATH-M118")) {
                        capacity = 40;
                    } else if (course.getCode().equals("MATH-M212")) {
                        capacity = 35;
                    }

                    courseOfferingRepository.save(new CourseOffering(course, term, capacity, enrolledCount));
                }
            }

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