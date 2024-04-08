package com.github.vincemann.springrapid.auth.val;

import org.passay.*;

import java.util.Arrays;
import java.util.List;

public class PasswordValidatorImpl implements com.github.vincemann.springrapid.auth.val.PasswordValidator {
    @Override
    public void validate(String password) throws InsufficientPasswordStrengthException {
        final org.passay.PasswordValidator validator = new org.passay.PasswordValidator(Arrays.asList(
                new LengthRule(8, 80),
                new UppercaseCharacterRule(1),
                new DigitCharacterRule(1)
                /*new SpecialCharacterRule(1),
                new NumericalSequenceRule(3,false),
                new AlphabeticalSequenceRule(3,false),
                new QwertySequenceRule(3,false),
                new WhitespaceRule())*/));
        final RuleResult ruleResult = validator.validate(new PasswordData(password));
        if (ruleResult.isValid()){
            return;
        }
        throw new InsufficientPasswordStrengthException(constructErrorMsg(ruleResult));
    }

    protected String constructErrorMsg(RuleResult ruleResult){
        List<RuleResultDetail> details = ruleResult.getDetails();
        StringBuilder sb = new StringBuilder("Invalid Password Strength:");
        int count = 1;
        for (RuleResultDetail detail : details) {
            sb.append(detail.toString());
            if (count >= details.size()){
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
