package com.zbdigit.myapp.util;

import java.lang.reflect.Field;
import javax.persistence.Id;

public class JpaUtil {

    public static String getPkColumn(String className) {
        String pkColumn = null;
        try {
            Class<?> clazz = Class.forName(className);
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.getAnnotation(Id.class) != null) {
                    pkColumn = field.getName();
                    break;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return pkColumn;
    }
}
