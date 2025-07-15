package com.kseb.collabtool.global.validation;

import com.kseb.collabtool.domain.groups.util.RRuleUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import net.fortuna.ical4j.model.Recur;

public class RRuleValidator implements ConstraintValidator<ValidRRule, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return RRuleUtil.isValidRRule(value);
    }
}
