package net.jnetpack.util;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.List;

@SuppressWarnings({"unused"})
public class ReflectionUtil {

    /**
     * Creates a Reflections object for a given package.
     *
     * @param packageName the name of the package
     * @return a Reflections object for the package
     */
    private Reflections createReflections(@NotNull String packageName) {
        return new Reflections(packageName);
    }

    /**
     * Creates instances of a list of classes.
     *
     * @param classes the list of classes
     * @return a list of instances of the classes
     */
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

    /**
     * Gets a list of classes in a package that implement a given class.
     *
     * @param packageName the name of the package
     * @param clazz the class
     * @return a list of classes that implement the class
     */
    public <T> ImmutableList<Class<? extends T>> getClassesImplement(@NotNull String packageName, @NotNull Class<T> clazz) {
        return ImmutableList.copyOf(createReflections(packageName)
                .getSubTypesOf(clazz));
    }

    /**
     * Gets a list of classes in a package that implement a given class.
     *
     * @param packagE the package
     * @param clazz the class
     * @return a list of classes that implement the class
     */
    public <T> ImmutableList<Class<? extends T>> getClassesImplement(@NotNull Package packagE, @NotNull Class<T> clazz) {
        return getClassesImplement(packagE.getName(), clazz);
    }

    /**
     * Gets a list of classes in a package.
     *
     * @param packageName the name of the package
     * @return a list of classes in the package
     */
    public ImmutableList<Class<?>> getClassesInPackage(@NotNull String packageName) {
        return ImmutableList.copyOf(createReflections(packageName)
                .getSubTypesOf(Object.class)
                .stream()
                .toList());
    }

    /**
     * Gets a list of classes in a package.
     *
     * @param packagE the package
     * @return a list of classes in the package
     */
    public ImmutableList<Class<?>> getClassesInPackage(@NotNull Package packagE) {
        return getClassesInPackage(packagE.getName());
    }

    /**
     * Gets a class in a package by its name.
     *
     * @param packageName the name of the package
     * @param className the name of the class
     * @return the class
     * @throws ClassNotFoundException if the class is not found
     */
    public Class<?> getClass(@NotNull String packageName, @NotNull String className) throws ClassNotFoundException {
        return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));
    }
}