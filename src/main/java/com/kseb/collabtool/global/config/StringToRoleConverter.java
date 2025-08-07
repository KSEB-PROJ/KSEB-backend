package com.kseb.collabtool.global.config;

import com.kseb.collabtool.domain.user.entity.Role;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToRoleConverter implements Converter<String, Role> {

    @Override
    public Role convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        try {
            // "ROLE_" 접두사 제거 후 대문자로 변환하여 Enum 상수와 매칭
            String enumName = source.startsWith("ROLE_") ? source.substring(5) : source;
            return Role.valueOf(enumName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}