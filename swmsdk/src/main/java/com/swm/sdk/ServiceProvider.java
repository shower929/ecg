package com.swm.sdk;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by yangzhenyu on 2017/3/10.
 */

abstract class ServiceProvider {
    Object getObject(String dependency, Object... args) throws Exception{
        try {
            Class<?> clazz = Class.forName(dependency);
            Class<?>[] argClass = new Class<?>[args.length];
            for(int i = 0; i < argClass.length; i++) {
                argClass[i] = args[i].getClass();
            }
            Constructor<?> c = clazz.getDeclaredConstructor(argClass);
            c.setAccessible(true);
            Object object = c.newInstance(args);
            return object;
        } catch (ClassNotFoundException e) {
            throw e;
        } catch (NoSuchMethodException e) {
            throw e;
        } catch (IllegalAccessException e) {
            throw e;
        } catch (InstantiationException e) {
            throw e;
        } catch (InvocationTargetException e) {
            throw e;
        }
    }

    abstract SwmEngine getService(SwmClient callback) throws Exception;

}
