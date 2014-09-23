package com.seava.throwable.thrift.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * 参数校验帮助类
 */
public class Validations {

    private static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static Validator validator = factory.getValidator();
    
    public static Map<String, String> validate(Object o) {
    	Map<String, String> map = new HashMap<String, String>();
    	if (null == o)
    		return map;
    	
        Set<ConstraintViolation<Object>> errors = validator.validate(o);
        for(ConstraintViolation<Object> r: errors) {
            map.put(r.getPropertyPath().toString(), r.getMessage());
        }
        return map;
    }
    
}
