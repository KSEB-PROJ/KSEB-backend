package com.kseb.collabtool.global.validation;

import com.kseb.collabtool.util.RRuleUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RRuleValidator implements ConstraintValidator<ValidRRule, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return RRuleUtil.isValidRRule(value);
    }
}
