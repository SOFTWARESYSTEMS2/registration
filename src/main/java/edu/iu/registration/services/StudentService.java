package edu.iu.registration.services;

import java.util.Collections;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.iu.registration.data.entities.AppUser;
import edu.iu.registration.data.entities.Course;
import edu.iu.registration.data.entities.Major;
import edu.iu.registration.data.entities.Minor;
import edu.iu.registration.data.repositories.AppUserRepository;

@Service
public class StudentService {
    @Autowired
    private AppUserRepository repository; // Your data source

    /*
        Takes a AppUser as an argument and returns their progress towards their degree, 
        major, minor, and gen eds as an array of ints.
        Defaulting to need: 120 degree, 50 major, 25 minor, 10 ah, 10 sh
    */
    public int[] checkProgress(AppUser student) {
        int[] progress =  {0, 120, 0, 50, 0, 25, 0, 10, 0, 10};
        Major major = student.getMajor();
        Minor minor = student.getMinor();
        // Specialization spec = student.getSpecialization();
        Set<Course> classes = getStudentCourses(student.getUsername());
        for (Course c : classes) {
            if (c.getDepartment().equals(major.getDepartment())) {
                progress[2] += c.getCredits();
            } 
            if (minor != null) {
                if (c.getDepartment().equals(minor.getDepartment())) {
                    progress[4] += c.getCredits();
                } 
            }
            // if (c.getDepartment().equals(spec.getDepartment())) {
            //     specCredits += c.getCredits();
            // } 
            if (c.getDepartment().getName().equals("Arts and Humanities")) {
                progress[6] += c.getCredits();
            } 
            if (c.getDepartment().getName().equals("Science and History")) {
                progress[8] += c.getCredits();
            }
            progress[0] += c.getCredits();
        }
        /*
            TODO: Add logic for checking if they have enough credits for their major, minor, and gen eds here
            Maybe switch it to a map?
        */
        return progress;
    }

    // This gets all classes student is / was enrolled in
    public Set<Course> getStudentCourses(String username) {
        return repository.findByUsername(username)
                .map(AppUser::getCourses)
                .orElse(Collections.emptySet());
    }

}
