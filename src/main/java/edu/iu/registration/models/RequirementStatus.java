package edu.iu.registration.models;

import edu.iu.registration.data.entities.Course;
import edu.iu.registration.data.entities.Requirement;
import java.util.Set;

public record RequirementStatus(
    Requirement requirement,
    Set<Course> completedEligibleCourses,
    int creditsEarned
) {
    public boolean isSatisfied() {
        return creditsEarned >= requirement().getCreditsNeeded();
    }

    public int creditsRemaining() {
        return Math.max(0, requirement().getCreditsNeeded() - creditsEarned);
    }
}
