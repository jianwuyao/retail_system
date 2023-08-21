package com.jwyao.system.permission;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Inherited
public @interface Access {
    AccessLevel level() default AccessLevel.LOGIN;
}
