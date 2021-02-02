package com.mscg;

import java.util.Collection;

public class Utils {

    @SuppressWarnings("unchecked")
    public static <T, C extends Collection<?>, CT extends Collection<T>> CT cast(C source, Class<T> clazz) {
        return (CT) source;
    }

}