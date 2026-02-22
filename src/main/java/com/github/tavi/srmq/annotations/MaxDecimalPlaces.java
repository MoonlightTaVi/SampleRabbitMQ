package com.github.tavi.srmq.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;


/**
 * When this annotation is put onto a {@code Float} field,
 * the number will be considered valid when it has less of equal decimal places
 * than specified by the annotation. <br>
 * {@code null} values are considered valid. <br>
 * The default restriction value is 0 (no decimal part).
 * 
 * @author MoonlightTaVi
 * @version 1.0.0
 */
@Target({
        ElementType.FIELD, ElementType.PARAMETER
})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MaxDecimalPlaces.MaxDecimalPlacesValidator.class)
public @interface MaxDecimalPlaces {

    /**
     * @return Maximum number of decimal places (fraction)
     *         for this number. <br>
     *         Defaults to 0.
     */
    int value() default 0;
    String message() default "The fraction of the number is restricted to the maximum of N decimal places.";

    /**
     * Validation groups.
     * The method is required by the Bean Validation specification.
     * May be overridden if needed.
     */
    Class<?>[] groups() default {};

    /**
     * Constraint validation pay-load (meta-data for REST clients).
     * The method is required by the Bean Validation specification.
     * May be overridden if needed.
     */
    Class<? extends Payload>[] payload() default {};


    /**
     * The nested validator class used by the annotation.
     */
    class MaxDecimalPlacesValidator
            implements ConstraintValidator<MaxDecimalPlaces, Float> {

        private int decimalPlaces;


        @Override
        public void initialize(MaxDecimalPlaces annotation) {
            decimalPlaces = annotation.value();
        }

        @Override
        public boolean isValid(
                Float value,
                ConstraintValidatorContext context)
        {
            if (value == null) {
                return true;
            }

            int div = (int) Math.pow(10, decimalPlaces);
            float truncated = (float) Math.floor(value * div) / div;
            return value.floatValue() == truncated;
        }

    }
}
