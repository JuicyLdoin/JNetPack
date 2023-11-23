package net.jnetpack.util;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.List;

@SuppressWarnings({"unused"})
public class ReflectionUtil {

    private Reflections createReflections(@NotNull String packageName) {

        return new Reflections(packageName);

    }

    public <T> ImmutableList<T> createInstances(List<Class<? extends T>> classes) {

        return ImmutableList.copyOf(classes.stream()
                .map(clazz -> {

                    try {

                        return (T) clazz.getConstructor(new Class[]{}).newInstance();

                    } catch (ReflectiveOperationException e) {

                        throw new RuntimeException(e);

                    }
                })
                .toList());

    }

    public <T> ImmutableList<Class<? extends T>> getClassesImplement(@NotNull String packageName, @NotNull Class<T> clazz) {

        return ImmutableList.copyOf(createReflections(packageName)
                .getSubTypesOf(clazz));

    }

    public <T> ImmutableList<Class<? extends T>> getClassesImplement(@NotNull Package packagE, @NotNull Class<T> clazz) {

        return getClassesImplement(packagE.getName(), clazz);

    }

    public ImmutableList<Class<?>> getClassesInPackage(@NotNull String packageName) {

        return ImmutableList.copyOf(createReflections(packageName)
                .getSubTypesOf(Object.class)
                .stream()
                .toList());

    }

    public ImmutableList<Class<?>> getClassesInPackage(@NotNull Package packagE) {

        return getClassesInPackage(packagE.getName());

    }

    public Class<?> getClass(@NotNull String packageName, @NotNull String className) throws ClassNotFoundException {

        return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));

    }
}