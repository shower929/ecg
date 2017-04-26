package com.swm.sdk;

import android.support.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by yangzhenyu on 2017/3/9.
 */

class SwmInject {
    @Nullable
    static Object getObject(String dependency, Object...args) throws Exception{
        Object object = null;

        try {
            Class<?> clazz = Class.forName(dependency);
            Class<?>[] argClass = new Class<?>[args.length];
            for(int i = 0; i < argClass.length; i++) {
                argClass[i] = args[i].getClass();
            }
            Constructor<?> c = clazz.getDeclaredConstructor(argClass);
            c.setAccessible(true);
            object = c.newInstance(args);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            throw e;
        } catch (IllegalAccessException e) {
            throw e;
        } catch (InstantiationException e) {
            throw e;
        } catch (InvocationTargetException e) {
            throw e;
        }

        return object;
    }
}
