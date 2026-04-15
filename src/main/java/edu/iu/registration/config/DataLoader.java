package edu.iu.registration.config;

import edu.iu.registration.data.entities.*;
import edu.iu.registration.data.enums.RequirementCategory;
import edu.iu.registration.data.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(
            DepartmentRepository deptRepo,
            MajorRepository majorRepo,
            MinorRepository minorRepo,
            SpecializationRepository specRepo,
            TermRepository termRepo,
            CourseRepository courseRepo,
            PrerequisiteRepository prereqRepo,
            CourseOfferingRepository offeringRepo,
            RequirementRepository reqRepo,
            AppUserRepository userRepo,
            StudentPlanRepository planRepo,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (deptRepo.count() > 0) return;

            // ── Departments ────────────────────────────────────────────────
            Department cs    = deptRepo.save(new Department("Computer Science"));
            Department math  = deptRepo.save(new Department("Mathematics"));
            Department eng   = deptRepo.save(new Department("English"));
            Department bio   = deptRepo.save(new Department("Biology"));
            Department chem  = deptRepo.save(new Department("Chemistry"));
            Department hist  = deptRepo.save(new Department("History"));
            Department phys  = deptRepo.save(new Department("Physics"));
            Department psych = deptRepo.save(new Department("Psychology"));
            Department stat  = deptRepo.save(new Department("Statistics"));
            Department info  = deptRepo.save(new Department("Information Science"));

            // ── Majors (10) ────────────────────────────────────────────────
            Major csMajor   = majorRepo.save(new Major("B.S. Computer Science", cs));
            Major mathMajor = majorRepo.save(new Major("B.S. Mathematics", math));
            Major engMajor  = majorRepo.save(new Major("B.A. English", eng));
            Major bioMajor  = majorRepo.save(new Major("B.S. Biology", bio));
            Major chemMajor = majorRepo.save(new Major("B.S. Chemistry", chem));
            Major histMajor = majorRepo.save(new Major("B.A. History", hist));
            Major physMajor = majorRepo.save(new Major("B.S. Physics", phys));
            Major psyMajor  = majorRepo.save(new Major("B.A. Psychology", psych));
            Major dsMajor   = majorRepo.save(new Major("B.S. Data Science", cs));
            Major infoMajor = majorRepo.save(new Major("B.S. Information Science", info));

            // ── Minors ─────────────────────────────────────────────────────
            Minor mathMinor  = minorRepo.save(new Minor("Mathematics Minor", math));
            Minor statMinor  = minorRepo.save(new Minor("Statistics Minor", stat));
            Minor writeMinor = minorRepo.save(new Minor("Writing Minor", eng));
            Minor csMinor    = minorRepo.save(new Minor("Computer Science Minor", cs));

            // ── Specializations ────────────────────────────────────────────
            Specialization seSpec   = specRepo.save(new Specialization("Software Engineering", csMajor));
            Specialization aiSpec   = specRepo.save(new Specialization("Artificial Intelligence", csMajor));
            Specialization bioiSpec = specRepo.save(new Specialization("Bioinformatics", bioMajor));

            // ── Terms: Year/Semester labels (no calendar years) ───────────
            Term y1s1 = termRepo.save(new Term("Year 1, Semester 1", false));
            Term y1s2 = termRepo.save(new Term("Year 1, Semester 2", false));
            Term y2s1 = termRepo.save(new Term("Year 2, Semester 1", true));  // active
            Term y2s2 = termRepo.save(new Term("Year 2, Semester 2", false));
            Term y3s1 = termRepo.save(new Term("Year 3, Semester 1", false));
            Term y3s2 = termRepo.save(new Term("Year 3, Semester 2", false));
            Term y4s1 = termRepo.save(new Term("Year 4, Semester 1", false));
            Term y4s2 = termRepo.save(new Term("Year 4, Semester 2", false));
            List<Term> allTerms = List.of(y1s1, y1s2, y2s1, y2s2, y3s1, y3s2, y4s1, y4s2);

            // ── Courses (63 total) ─────────────────────────────────────────

            // Computer Science (12)
            Course csA101 = courseRepo.save(new Course("CSCI-A101", "Intro to Programming",    3, cs, false));
            Course csA201 = courseRepo.save(new Course("CSCI-A201", "Data Structures",         3, cs, false));
            Course csA301 = courseRepo.save(new Course("CSCI-A301", "Algorithms",              3, cs, false));
            Course csA310 = courseRepo.save(new Course("CSCI-A310", "Software Engineering",    3, cs, false));
            Course csA340 = courseRepo.save(new Course("CSCI-A340", "Artificial Intelligence", 3, cs, false));
            Course csA401 = courseRepo.save(new Course("CSCI-A401", "Operating Systems",       3, cs, false));
            Course csA422 = courseRepo.save(new Course("CSCI-A422", "Computer Networks",       3, cs, false));
            Course csA442 = courseRepo.save(new Course("CSCI-A442", "Database Systems",        3, cs, false));
            Course csA490 = courseRepo.save(new Course("CSCI-A490", "Senior Capstone",         3, cs, false));
            Course csB461 = courseRepo.save(new Course("CSCI-B461", "Machine Learning",        3, cs, false));
            Course csC241 = courseRepo.save(new Course("CSCI-C241", "Discrete Mathematics",    3, cs, false));
            Course csA348 = courseRepo.save(new Course("CSCI-A348", "Web Development",         3, cs, false));

            // Mathematics (9)
            Course mM118  = courseRepo.save(new Course("MATH-M118", "Finite Mathematics",            3, math, false));
            Course mM211  = courseRepo.save(new Course("MATH-M211", "Calculus I",                    4, math, false));
            Course mM212  = courseRepo.save(new Course("MATH-M212", "Calculus II",                   4, math, false));
            Course mM301  = courseRepo.save(new Course("MATH-M301", "Linear Algebra",                3, math, false));
            Course mM311  = courseRepo.save(new Course("MATH-M311", "Calculus III",                  4, math, false));
            Course mM360  = courseRepo.save(new Course("MATH-M360", "Elements of Probability",       3, math, false));
            Course mM371  = courseRepo.save(new Course("MATH-M371", "Complex Variables",             3, math, false));
            Course mM403  = courseRepo.save(new Course("MATH-M403", "Abstract Algebra",              3, math, false));
            Course mM447  = courseRepo.save(new Course("MATH-M447", "Mathematical Analysis",         3, math, false));

            // Statistics (4)
            Course stS301 = courseRepo.save(new Course("STAT-S301", "Applied Statistical Methods",   3, stat, false));
            Course stS420 = courseRepo.save(new Course("STAT-S420", "Statistics Theory",             3, stat, false));
            Course stS431 = courseRepo.save(new Course("STAT-S431", "Applied Linear Models",         3, stat, false));
            Course stS440 = courseRepo.save(new Course("STAT-S440", "Time Series Analysis",          3, stat, false));

            // English (7)
            Course eL101  = courseRepo.save(new Course("ENG-L101",  "Reading and Writing",           3, eng, false));
            Course eL201  = courseRepo.save(new Course("ENG-L201",  "Introduction to Literature",    3, eng, false));
            Course eL301  = courseRepo.save(new Course("ENG-L301",  "British Literature",            3, eng, false));
            Course eL302  = courseRepo.save(new Course("ENG-L302",  "American Literature",           3, eng, false));
            Course eL401  = courseRepo.save(new Course("ENG-L401",  "Senior Seminar",                3, eng, false));
            Course eW231  = courseRepo.save(new Course("ENG-W231",  "Technical Writing",             3, eng, false));
            Course eW350  = courseRepo.save(new Course("ENG-W350",  "Creative Writing",              3, eng, false));

            // Biology (6)
            Course bL101  = courseRepo.save(new Course("BIOL-L101", "Principles of Biology I",       3, bio, false));
            Course bL102  = courseRepo.save(new Course("BIOL-L102", "Principles of Biology II",      3, bio, false));
            Course bL211  = courseRepo.save(new Course("BIOL-L211", "Cell Biology",                  3, bio, false));
            Course bL311  = courseRepo.save(new Course("BIOL-L311", "Genetics",                      3, bio, false));
            Course bL401  = courseRepo.save(new Course("BIOL-L401", "Ecology",                       3, bio, false));
            Course bL450  = courseRepo.save(new Course("BIOL-L450", "Bioinformatics",                3, bio, false));

            // Chemistry (5)
            Course cC101  = courseRepo.save(new Course("CHEM-C101", "General Chemistry I",           3, chem, false));
            Course cC102  = courseRepo.save(new Course("CHEM-C102", "General Chemistry II",          3, chem, false));
            Course cC301  = courseRepo.save(new Course("CHEM-C301", "Organic Chemistry I",           3, chem, false));
            Course cC302  = courseRepo.save(new Course("CHEM-C302", "Organic Chemistry II",          3, chem, false));
            Course cC401  = courseRepo.save(new Course("CHEM-C401", "Biochemistry",                  3, chem, false));

            // History (5)
            Course hH101  = courseRepo.save(new Course("HIST-H101", "World History I",               3, hist, false));
            Course hH102  = courseRepo.save(new Course("HIST-H102", "World History II",              3, hist, false));
            Course hH301  = courseRepo.save(new Course("HIST-H301", "American History",              3, hist, false));
            Course hH350  = courseRepo.save(new Course("HIST-H350", "European History",              3, hist, false));
            Course hH401  = courseRepo.save(new Course("HIST-H401", "Senior Research Seminar",       3, hist, false));

            // Physics (5)
            Course pP201  = courseRepo.save(new Course("PHYS-P201", "Physics I",                     4, phys, false));
            Course pP202  = courseRepo.save(new Course("PHYS-P202", "Physics II",                    4, phys, false));
            Course pP301  = courseRepo.save(new Course("PHYS-P301", "Modern Physics",                3, phys, false));
            Course pP401  = courseRepo.save(new Course("PHYS-P401", "Quantum Mechanics",             3, phys, false));
            Course pP310  = courseRepo.save(new Course("PHYS-P310", "Thermodynamics",                3, phys, false));

            // Psychology (5)
            Course pyP101 = courseRepo.save(new Course("PSY-P101",  "Intro to Psychology",           3, psych, false));
            Course pyP211 = courseRepo.save(new Course("PSY-P211",  "Research Methods",              3, psych, false));
            Course pyP301 = courseRepo.save(new Course("PSY-P301",  "Abnormal Psychology",           3, psych, false));
            Course pyP320 = courseRepo.save(new Course("PSY-P320",  "Social Psychology",             3, psych, false));
            Course pyP401 = courseRepo.save(new Course("PSY-P401",  "Capstone in Psychology",        3, psych, false));

            // Information Science (5)
            Course iI101  = courseRepo.save(new Course("INFO-I101",  "Intro to Informatics",         3, info, false));
            Course iI201  = courseRepo.save(new Course("INFO-I201",  "Data Representation",          3, info, false));
            Course iI301  = courseRepo.save(new Course("INFO-I301",  "Human-Computer Interaction",   3, info, false));
            Course iI308  = courseRepo.save(new Course("INFO-I308",  "Web Design",                   3, info, false));
            Course iI401  = courseRepo.save(new Course("INFO-I401",  "Information Architecture",     3, info, false));

            // ── Prerequisites ──────────────────────────────────────────────
            // CS chain
            prereqRepo.save(new Prerequisite(csA201, csA101));
            prereqRepo.save(new Prerequisite(csA301, csA201));
            prereqRepo.save(new Prerequisite(csA310, csA201));
            prereqRepo.save(new Prerequisite(csA340, csA201));
            prereqRepo.save(new Prerequisite(csA401, csA301));
            prereqRepo.save(new Prerequisite(csA422, csA301));
            prereqRepo.save(new Prerequisite(csA442, csA201));
            prereqRepo.save(new Prerequisite(csA490, csA401));
            prereqRepo.save(new Prerequisite(csB461, csA340));
            prereqRepo.save(new Prerequisite(csC241, csA101));
            prereqRepo.save(new Prerequisite(csA348, csA201));
            // Math chain
            prereqRepo.save(new Prerequisite(mM212, mM211));
            prereqRepo.save(new Prerequisite(mM301, mM211));
            prereqRepo.save(new Prerequisite(mM311, mM212));
            prereqRepo.save(new Prerequisite(mM360, mM212));
            prereqRepo.save(new Prerequisite(mM371, mM311));
            prereqRepo.save(new Prerequisite(mM403, mM301));
            prereqRepo.save(new Prerequisite(mM447, mM311));
            // Stat
            prereqRepo.save(new Prerequisite(stS301, mM118));
            prereqRepo.save(new Prerequisite(stS420, mM211));
            prereqRepo.save(new Prerequisite(stS431, stS301));
            prereqRepo.save(new Prerequisite(stS440, stS420));
            // English
            prereqRepo.save(new Prerequisite(eL201, eL101));
            prereqRepo.save(new Prerequisite(eL301, eL201));
            prereqRepo.save(new Prerequisite(eL302, eL201));
            prereqRepo.save(new Prerequisite(eL401, eL301));
            prereqRepo.save(new Prerequisite(eW350, eL201));
            // Biology
            prereqRepo.save(new Prerequisite(bL102, bL101));
            prereqRepo.save(new Prerequisite(bL211, bL102));
            prereqRepo.save(new Prerequisite(bL311, bL211));
            prereqRepo.save(new Prerequisite(bL401, bL311));
            prereqRepo.save(new Prerequisite(bL450, bL311));
            // Chemistry
            prereqRepo.save(new Prerequisite(cC102, cC101));
            prereqRepo.save(new Prerequisite(cC301, cC102));
            prereqRepo.save(new Prerequisite(cC302, cC301));
            prereqRepo.save(new Prerequisite(cC401, cC301));
            // History
            prereqRepo.save(new Prerequisite(hH102, hH101));
            prereqRepo.save(new Prerequisite(hH401, hH301));
            // Physics (needs Calc I)
            prereqRepo.save(new Prerequisite(pP201, mM211));
            prereqRepo.save(new Prerequisite(pP202, pP201));
            prereqRepo.save(new Prerequisite(pP301, pP202));
            prereqRepo.save(new Prerequisite(pP401, pP301));
            prereqRepo.save(new Prerequisite(pP310, pP202));
            // Psychology
            prereqRepo.save(new Prerequisite(pyP211, pyP101));
            prereqRepo.save(new Prerequisite(pyP301, pyP211));
            prereqRepo.save(new Prerequisite(pyP320, pyP211));
            prereqRepo.save(new Prerequisite(pyP401, pyP301));
            // Info Science
            prereqRepo.save(new Prerequisite(iI201, iI101));
            prereqRepo.save(new Prerequisite(iI301, iI201));
            prereqRepo.save(new Prerequisite(iI308, iI201));
            prereqRepo.save(new Prerequisite(iI401, iI301));

            // ── Offerings: every course × every term ──────────────────────
            // Capacity rules; CSCI-A101 in y2s1 and y2s2 are intentionally FULL
            // to demonstrate the seat-availability filter
            List<Course> allCourses = List.of(
                csA101, csA201, csA301, csA310, csA340, csA401, csA422, csA442,
                csA490, csB461, csC241, csA348,
                mM118, mM211, mM212, mM301, mM311, mM360, mM371, mM403, mM447,
                stS301, stS420, stS431, stS440,
                eL101, eL201, eL301, eL302, eL401, eW231, eW350,
                bL101, bL102, bL211, bL311, bL401, bL450,
                cC101, cC102, cC301, cC302, cC401,
                hH101, hH102, hH301, hH350, hH401,
                pP201, pP202, pP301, pP401, pP310,
                pyP101, pyP211, pyP301, pyP320, pyP401,
                iI101, iI201, iI301, iI308, iI401
            );

            for (Term term : allTerms) {
                for (Course course : allCourses) {
                    int capacity = 30;
                    int enrolled = 0;
                    // Intro courses are large
                    if (course.getCode().equals("CSCI-A101") || course.getCode().equals("PSY-P101")
                            || course.getCode().equals("ENG-L101") || course.getCode().equals("BIOL-L101")) {
                        capacity = 120;
                    }
                    // Upper-division courses are small
                    if (course.getCode().endsWith("401") || course.getCode().endsWith("490")
                            || course.getCode().endsWith("447") || course.getCode().endsWith("461")) {
                        capacity = 15;
                    }
                    // Make CSCI-A201 sections in active term deliberately full
                    if (course.getCode().equals("CSCI-A201") && term == y2s1) {
                        capacity = 25; enrolled = 25;
                    }
                    // Make MATH-M212 in active term full as well
                    if (course.getCode().equals("MATH-M212") && term == y2s1) {
                        capacity = 20; enrolled = 20;
                    }
                    offeringRepo.save(new CourseOffering(course, term, capacity, enrolled));
                }
            }

            // ── Requirements ──────────────────────────────────────────────

            // B.S. Computer Science
            Requirement csCoreReq = new Requirement("CS Core", RequirementCategory.CORE, 15, csMajor);
            csCoreReq.addEligibleCourse(csA101); csCoreReq.addEligibleCourse(csA201);
            csCoreReq.addEligibleCourse(csA301); csCoreReq.addEligibleCourse(csA401);
            csCoreReq.addEligibleCourse(csC241);
            reqRepo.save(csCoreReq);

            Requirement csUpperReq = new Requirement("CS Upper Division", RequirementCategory.CORE, 9, csMajor);
            csUpperReq.addEligibleCourse(csA422); csUpperReq.addEligibleCourse(csA442);
            csUpperReq.addEligibleCourse(csA490);
            reqRepo.save(csUpperReq);

            Requirement csElecReq = new Requirement("CS Elective", RequirementCategory.ELECTIVE, 6, csMajor);
            csElecReq.addEligibleCourse(csA310); csElecReq.addEligibleCourse(csA340);
            csElecReq.addEligibleCourse(csA348); csElecReq.addEligibleCourse(csB461);
            reqRepo.save(csElecReq);

            Requirement csMathReq = new Requirement("Math for CS", RequirementCategory.CORE, 7, csMajor);
            csMathReq.addEligibleCourse(mM211); csMathReq.addEligibleCourse(mM118);
            csMathReq.addEligibleCourse(mM301);
            reqRepo.save(csMathReq);

            // B.S. Mathematics
            Requirement mathCoreReq = new Requirement("Math Core", RequirementCategory.CORE, 15, mathMajor);
            mathCoreReq.addEligibleCourse(mM211); mathCoreReq.addEligibleCourse(mM212);
            mathCoreReq.addEligibleCourse(mM301); mathCoreReq.addEligibleCourse(mM311);
            reqRepo.save(mathCoreReq);

            Requirement mathUpperReq = new Requirement("Math Upper Division", RequirementCategory.CORE, 9, mathMajor);
            mathUpperReq.addEligibleCourse(mM403); mathUpperReq.addEligibleCourse(mM447);
            mathUpperReq.addEligibleCourse(mM371);
            reqRepo.save(mathUpperReq);

            Requirement mathElecReq = new Requirement("Math Elective", RequirementCategory.ELECTIVE, 6, mathMajor);
            mathElecReq.addEligibleCourse(mM360); mathElecReq.addEligibleCourse(stS301);
            reqRepo.save(mathElecReq);

            // B.A. English
            Requirement engCoreReq = new Requirement("English Core", RequirementCategory.CORE, 12, engMajor);
            engCoreReq.addEligibleCourse(eL101); engCoreReq.addEligibleCourse(eL201);
            engCoreReq.addEligibleCourse(eL301); engCoreReq.addEligibleCourse(eL302);
            reqRepo.save(engCoreReq);

            Requirement engUpperReq = new Requirement("English Upper Division", RequirementCategory.CORE, 9, engMajor);
            engUpperReq.addEligibleCourse(eL401); engUpperReq.addEligibleCourse(eW350);
            engUpperReq.addEligibleCourse(eW231);
            reqRepo.save(engUpperReq);

            // B.S. Biology
            Requirement bioCoreReq = new Requirement("Biology Core", RequirementCategory.CORE, 15, bioMajor);
            bioCoreReq.addEligibleCourse(bL101); bioCoreReq.addEligibleCourse(bL102);
            bioCoreReq.addEligibleCourse(bL211); bioCoreReq.addEligibleCourse(bL311);
            bioCoreReq.addEligibleCourse(bL401);
            reqRepo.save(bioCoreReq);

            Requirement bioUpperReq = new Requirement("Biology Upper Division", RequirementCategory.CORE, 6, bioMajor);
            bioUpperReq.addEligibleCourse(bL401); bioUpperReq.addEligibleCourse(bL450);
            reqRepo.save(bioUpperReq);

            Requirement bioChemReq = new Requirement("Chemistry Requirement", RequirementCategory.CORE, 6, bioMajor);
            bioChemReq.addEligibleCourse(cC101); bioChemReq.addEligibleCourse(cC102);
            reqRepo.save(bioChemReq);

            // B.S. Chemistry
            Requirement chemCoreReq = new Requirement("Chemistry Core", RequirementCategory.CORE, 15, chemMajor);
            chemCoreReq.addEligibleCourse(cC101); chemCoreReq.addEligibleCourse(cC102);
            chemCoreReq.addEligibleCourse(cC301); chemCoreReq.addEligibleCourse(cC302);
            chemCoreReq.addEligibleCourse(cC401);
            reqRepo.save(chemCoreReq);

            Requirement chemMathReq = new Requirement("Math Requirement", RequirementCategory.CORE, 8, chemMajor);
            chemMathReq.addEligibleCourse(mM211); chemMathReq.addEligibleCourse(mM212);
            reqRepo.save(chemMathReq);

            // B.A. History
            Requirement histCoreReq = new Requirement("History Core", RequirementCategory.CORE, 12, histMajor);
            histCoreReq.addEligibleCourse(hH101); histCoreReq.addEligibleCourse(hH102);
            histCoreReq.addEligibleCourse(hH301); histCoreReq.addEligibleCourse(hH350);
            reqRepo.save(histCoreReq);

            Requirement histUpperReq = new Requirement("History Upper Division", RequirementCategory.CORE, 6, histMajor);
            histUpperReq.addEligibleCourse(hH401); histUpperReq.addEligibleCourse(hH350);
            reqRepo.save(histUpperReq);

            // B.S. Physics
            Requirement physCoreReq = new Requirement("Physics Core", RequirementCategory.CORE, 17, physMajor);
            physCoreReq.addEligibleCourse(pP201); physCoreReq.addEligibleCourse(pP202);
            physCoreReq.addEligibleCourse(pP301); physCoreReq.addEligibleCourse(pP401);
            physCoreReq.addEligibleCourse(pP310);
            reqRepo.save(physCoreReq);

            Requirement physMathReq = new Requirement("Math Requirement", RequirementCategory.CORE, 12, physMajor);
            physMathReq.addEligibleCourse(mM211); physMathReq.addEligibleCourse(mM212);
            physMathReq.addEligibleCourse(mM311);
            reqRepo.save(physMathReq);

            // B.A. Psychology
            Requirement psyCoreReq = new Requirement("Psychology Core", RequirementCategory.CORE, 15, psyMajor);
            psyCoreReq.addEligibleCourse(pyP101); psyCoreReq.addEligibleCourse(pyP211);
            psyCoreReq.addEligibleCourse(pyP301); psyCoreReq.addEligibleCourse(pyP320);
            psyCoreReq.addEligibleCourse(pyP401);
            reqRepo.save(psyCoreReq);

            Requirement psyStatReq = new Requirement("Statistics Requirement", RequirementCategory.CORE, 6, psyMajor);
            psyStatReq.addEligibleCourse(stS301); psyStatReq.addEligibleCourse(stS420);
            reqRepo.save(psyStatReq);

            // B.S. Data Science
            Requirement dsCoreReq = new Requirement("Data Science Core", RequirementCategory.CORE, 12, dsMajor);
            dsCoreReq.addEligibleCourse(csA101); dsCoreReq.addEligibleCourse(csA201);
            dsCoreReq.addEligibleCourse(stS301); dsCoreReq.addEligibleCourse(stS420);
            reqRepo.save(dsCoreReq);

            Requirement dsAppliedReq = new Requirement("Applied Methods", RequirementCategory.CORE, 9, dsMajor);
            dsAppliedReq.addEligibleCourse(stS431); dsAppliedReq.addEligibleCourse(stS440);
            dsAppliedReq.addEligibleCourse(csB461);
            reqRepo.save(dsAppliedReq);

            Requirement dsMathReq = new Requirement("Math for Data Science", RequirementCategory.CORE, 8, dsMajor);
            dsMathReq.addEligibleCourse(mM211); dsMathReq.addEligibleCourse(mM212);
            reqRepo.save(dsMathReq);

            // B.S. Information Science
            Requirement infoCoreReq = new Requirement("Info Science Core", RequirementCategory.CORE, 12, infoMajor);
            infoCoreReq.addEligibleCourse(iI101); infoCoreReq.addEligibleCourse(iI201);
            infoCoreReq.addEligibleCourse(iI301); infoCoreReq.addEligibleCourse(iI401);
            reqRepo.save(infoCoreReq);

            Requirement infoWebReq = new Requirement("Web and Design", RequirementCategory.ELECTIVE, 6, infoMajor);
            infoWebReq.addEligibleCourse(iI308); infoWebReq.addEligibleCourse(iI301);
            reqRepo.save(infoWebReq);

            // Mathematics Minor
            Requirement mathMinorReq = new Requirement("Math Minor Core", RequirementCategory.CORE, 11, mathMinor);
            mathMinorReq.addEligibleCourse(mM211); mathMinorReq.addEligibleCourse(mM212);
            mathMinorReq.addEligibleCourse(mM301);
            reqRepo.save(mathMinorReq);

            // Statistics Minor
            Requirement statMinorReq = new Requirement("Stats Minor Core", RequirementCategory.CORE, 12, statMinor);
            statMinorReq.addEligibleCourse(stS301); statMinorReq.addEligibleCourse(stS420);
            statMinorReq.addEligibleCourse(stS431);
            reqRepo.save(statMinorReq);

            // Writing Minor
            Requirement writeMinorReq = new Requirement("Writing Minor Core", RequirementCategory.CORE, 9, writeMinor);
            writeMinorReq.addEligibleCourse(eW231); writeMinorReq.addEligibleCourse(eL201);
            writeMinorReq.addEligibleCourse(eW350);
            reqRepo.save(writeMinorReq);

            // CS Minor
            Requirement csMinorReq = new Requirement("CS Minor Core", RequirementCategory.CORE, 12, csMinor);
            csMinorReq.addEligibleCourse(csA101); csMinorReq.addEligibleCourse(csA201);
            csMinorReq.addEligibleCourse(csC241); csMinorReq.addEligibleCourse(csA301);
            reqRepo.save(csMinorReq);

            // Software Engineering Specialization
            Requirement seSpecReq = new Requirement("SE Core", RequirementCategory.CORE, 6, seSpec);
            seSpecReq.addEligibleCourse(csA310); seSpecReq.addEligibleCourse(csA442);
            reqRepo.save(seSpecReq);

            // Artificial Intelligence Specialization
            Requirement aiSpecReq = new Requirement("AI Core", RequirementCategory.CORE, 6, aiSpec);
            aiSpecReq.addEligibleCourse(csA340); aiSpecReq.addEligibleCourse(csB461);
            reqRepo.save(aiSpecReq);

            // Bioinformatics Specialization
            Requirement bioiSpecReq = new Requirement("Bioinformatics Core", RequirementCategory.CORE, 6, bioiSpec);
            bioiSpecReq.addEligibleCourse(bL450); bioiSpecReq.addEligibleCourse(csA101);
            reqRepo.save(bioiSpecReq);

            // ── Seed users ─────────────────────────────────────────────────
            // "user" — CS major, Math minor, SE spec, Sophomore
            // Completed: all of Year 1 Semester 1 and Year 1 Semester 2
            AppUser student = new AppUser(
                    "user", passwordEncoder.encode("userpass"), "STUDENT",
                    "Sophomore", csMajor, mathMinor, seSpec);
            // Year 1, Semester 1 completions
            student.addCompletedCourse(csA101);
            student.addCompletedCourse(mM118);
            student.addCompletedCourse(eL101);
            student.addCompletedCourse(hH101);
            // Year 1, Semester 2 completions
            student.addCompletedCourse(csA201);
            student.addCompletedCourse(csC241);
            student.addCompletedCourse(mM211);
            student.addCompletedCourse(pyP101);
            userRepo.save(student);

            userRepo.save(new AppUser(
                    "admin", passwordEncoder.encode("adminpass"), "ADMIN",
                    "Senior", csMajor, null, null));

            // ── Seed student plan entries ──────────────────────────────────
            // Year 2, Semester 1 — note CSCI-A201 and MATH-M212 are full in y2s1,
            // so these are intentionally skipped; student planned other courses instead
            addPlan(student, csA301,  y2s1, offeringRepo, planRepo);
            addPlan(student, csA310,  y2s1, offeringRepo, planRepo);
            addPlan(student, csA442,  y2s1, offeringRepo, planRepo);
            addPlan(student, mM212,   y2s2, offeringRepo, planRepo); // M212 full in y2s1, planned y2s2

            // Year 2, Semester 2
            addPlan(student, csA340,  y2s2, offeringRepo, planRepo);
            addPlan(student, csA401,  y2s2, offeringRepo, planRepo);
            addPlan(student, mM301,   y2s2, offeringRepo, planRepo);

            // Year 3, Semester 1
            addPlan(student, csA422,  y3s1, offeringRepo, planRepo);
            addPlan(student, csB461,  y3s1, offeringRepo, planRepo);
            addPlan(student, mM311,   y3s1, offeringRepo, planRepo);

            // Year 3, Semester 2
            addPlan(student, csA490,  y3s2, offeringRepo, planRepo);
            addPlan(student, csA348,  y3s2, offeringRepo, planRepo);
        };
    }

    private void addPlan(AppUser user, Course course, Term term,
            CourseOfferingRepository offeringRepo, StudentPlanRepository planRepo) {
        offeringRepo.findByCourseAndTerm(course, term)
                .ifPresent(offering -> planRepo.save(new StudentPlan(user, offering)));
    }
}
