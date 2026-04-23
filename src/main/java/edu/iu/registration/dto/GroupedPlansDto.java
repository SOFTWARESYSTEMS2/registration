package edu.iu.registration.dto;

import java.util.List;

public class GroupedPlansDto {

    private String termLabel;
    private List<StudentPlanDto> plans;

    public GroupedPlansDto(String termLabel, List<StudentPlanDto> plans) {
        this.termLabel = termLabel;
        this.plans = plans;
    }

    public String getTermLabel() {
        return termLabel;
    }

    public List<StudentPlanDto> getPlans() {
        return plans;
    }
}