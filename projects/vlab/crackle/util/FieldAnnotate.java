package vlab.crackle.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)

public @interface FieldAnnotate
{
  int size();
  FieldType type() default FieldType.STRING;
  boolean isNull() default false;
}
