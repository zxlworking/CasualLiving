package com.zxl.casual.living.http.data;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by zxl on 2018/11/23.
 */

public class ParameterizedTypeImpl implements ParameterizedType {
    private final Class raw;
    private final Type[] args;

    public ParameterizedTypeImpl(Class raw, Type[] args) {
        this.raw = raw;
        this.args = args != null ? args : new Type[0];
    }

    @Override
    public Type[] getActualTypeArguments() {
        return args;
    }

    @Override
    public Type getRawType() {
        return raw;
    }

    @Override public Type getOwnerType() {
        return null;
    }
}
