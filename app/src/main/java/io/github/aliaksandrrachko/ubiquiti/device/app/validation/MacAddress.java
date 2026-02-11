package io.github.aliaksandrrachko.ubiquiti.device.app.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Pattern(regexp = "^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$")
public @interface MacAddress {
    
    String message() default "Invalid MAC address format";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
