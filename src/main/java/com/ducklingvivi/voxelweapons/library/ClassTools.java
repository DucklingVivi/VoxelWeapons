package com.ducklingvivi.voxelweapons.library;

import java.lang.reflect.Field;

public class ClassTools {

    public static <S extends T,T,V> V getPrivateFieldValue(Class<T> type, S object, String fieldstring, Class<V> fieldType) throws NoSuchFieldException, IllegalAccessException {
        Field field = type.getDeclaredField(fieldstring);
        boolean prev = field.canAccess(object);
        field.setAccessible(true);
        V value = fieldType.cast(field.get(object));
        field.setAccessible(prev);
        return value;
    }
    public static <S extends T,T,V> void setPrivateFieldValue(Class<T> type, S object, String fieldstring, Object fieldItem) throws NoSuchFieldException, IllegalAccessException {
        Field field = type.getDeclaredField(fieldstring);
        boolean prev = field.canAccess(object);
        field.setAccessible(true);
        field.set(object, fieldItem);
        field.setAccessible(prev);
    }
}
