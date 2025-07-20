package com.kseb.collabtool.global.validation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;


@Target({ ElementType.FIELD, ElementType.PARAMETER }) //이 어노테이션을 어디에 붙일 수 있는지 지정
@Retention(RetentionPolicy.RUNTIME) //이 어노테이션이 언제까지 살아있냐
@Documented
@Constraint(validatedBy = RRuleValidator.class)
public @interface ValidRRule { //새로운 어노테이션을 만들 때 @interface 사용
    String message() default "잘못된 rrule 형식입니다."; //검증 실패시 보여줄 기본 에러 메시지
    Class<?>[] groups() default {}; //여러 단계의 검증을 분리하고 싶을 때 사용
    Class<? extends Payload>[] payload() default {}; //프레임워크가 내부적으로 쓸 일 있을 때만 사용
}
