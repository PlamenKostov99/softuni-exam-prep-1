package hiberspring.util;


import hiberspring.util.ValidationUtil;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;


public class ValidatorUtilImpl implements ValidationUtil {


    private final Validator validator;

    public ValidatorUtilImpl() {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }


    @Override
    public <E> boolean isValid(E entity) {
       return this.validator.validate(entity).isEmpty();
    }





}
