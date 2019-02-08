package kr.or.knia.batch.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Target({FIELD})
public @interface FixedRange {

  @AliasFor("start")
  int value() default 0;

  @AliasFor("value")
  int start() default 0;

  int end() default -1;
}
