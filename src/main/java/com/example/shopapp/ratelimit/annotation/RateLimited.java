package com.example.shopapp.ratelimit.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimited {

    int limit() default 10;

    int windowSeconds() default 60;
}
