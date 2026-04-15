package edu.iu.registration.models;

// Immutable snapshot of a student's overall degree completion.
// Passed from DegreeMapService to controllers and templates.
public record DegreeProgress(
    int totalCreditsEarned,
    int totalCreditsRequired,   // always 120

    int majorCreditsEarned,
    int majorCreditsRequired,

    int minorCreditsEarned,
    int minorCreditsRequired,   // 0 if no minor declared

    int specCreditsEarned,
    int specCreditsRequired,    // 0 if no spec declared

    int genEdCreditsEarned,
    int genEdCreditsRequired    // always 30
) {
    public int totalCreditsRemaining() {
        return Math.max(0, totalCreditsRequired - totalCreditsEarned);
    }

    public boolean isMajorComplete() {
        return majorCreditsEarned >= majorCreditsRequired;
    }

    public boolean isMinorComplete() {
        return minorCreditsRequired == 0 || minorCreditsEarned >= minorCreditsRequired;
    }
}
