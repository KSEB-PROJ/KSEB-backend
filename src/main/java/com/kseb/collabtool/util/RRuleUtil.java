package com.kseb.collabtool.util;

import lombok.NoArgsConstructor;

import net.fortuna.ical4j.model.Recur;

@NoArgsConstructor
public class RRuleUtil {

    // 생성자 private 처리 (static util 용도)

    /**
     * RFC 5545 rrule 문법 검증
     * @param rule 검사할 rrule 문자열
     * @return 파싱 가능하면 true, 아니면 false
     */
    public static boolean isValidRRule(String rule) {
        if (rule == null || rule.isBlank()) return true;   // nullable 허용 시 true, 필수면 false로 변경 가능
        try {
            new Recur(rule);     // 파싱 성공 = 문법 오류X
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
