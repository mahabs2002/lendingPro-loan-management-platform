package com.loanapp.eligibility;

import lombok.*;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class EligibilityResult {
    private boolean eligible;
    private int score;
    private String reason;

    public static EligibilityResult approved(int score) {
        return EligibilityResult.builder().eligible(true).score(score).reason("Eligible").build();
    }
    public static EligibilityResult rejected(String reason) {
        return EligibilityResult.builder().eligible(false).score(0).reason(reason).build();
    }
}
