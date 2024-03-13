package com.andreidodu.europealibrary.batch;

import lombok.Getter;

@Getter
public enum JobStepEnum {
    INSERTED(1), READY(2);

    final private int stepNumber;

    JobStepEnum(int stepNumber) {
        this.stepNumber = stepNumber;
    }

}
