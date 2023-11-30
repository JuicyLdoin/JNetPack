package net.jnetpack.event;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.jnetpack.JNetOptions;
import net.jnetpack.event.annotation.EventHandler;
import net.jnetpack.event.interfaces.IEventHandler;
import net.jnetpack.event.interfaces.IEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manager of {@link IEventHandler JNet event handlers}
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventHandlerManager {

    final Map<IEventHandler, Integer> handlers = new HashMap<>();
    final Table<Integer, Class<? extends IEvent>, Method> methodTable = HashBasedTable.create();
    final Table<Integer, Class<? extends IEvent>, Boolean> asyncTable = HashBasedTable.create();

    int registered = 0;
    ExecutorService executor;

    /**
     * Register {@link IEventHandler JNet event handler}
     * Check all methods in class and if it has a 1 parameter and {@link EventHandler JNet event handler annotation}
     * Then register it in methodTable and asyncTable
     * <p>
     * If {@link EventHandler JNet event handler annotation} is async and executor not registered, register it
     *
     * @param handler - target {@link IEventHandler JNet event handler}
     */
    @SuppressWarnings({"unchecked cast"})
    public void registerHandler(IEventHandler handler) {
        boolean register = false;
        for (Method method : handler.getClass().getMethods()) {
            if (method.getParameterCount() != 1) {
                continue;
            }
            EventHandler eventHandler = method.getAnnotation(EventHandler.class);
            if (eventHandler == null) {
                continue;
            }
            boolean async = eventHandler.async();

            if (async && executor == null) {
                executor = Executors.newFixedThreadPool(JNetOptions.EVENT_HANDLER_THREADS);
            }

            Parameter parameter = method.getParameters()[0];
            try {
                Class<? extends IEvent> clazz = (Class<? extends IEvent>) parameter.getType();
                methodTable.put(registered, clazz, method);
                asyncTable.put(registered, clazz, async);
                register = true;
            } catch (ClassCastException ignored) {}
        }
        if (register) {
            handlers.put(handler, registered);
            registered++;
        }
    }

    /**
     * Unregister {@link IEventHandler JNet event handler} and call {@link #checkAsync()}
     *
     * @param handler - target handler
     */
    public void unregisterHandler(IEventHandler handler) {
        int id = handlers.remove(handler);
        methodTable.rowKeySet().remove(id);
        asyncTable.rowKeySet().remove(id);

        checkAsync();
    }

    /**
     * Check if {@link #asyncTable} hasn`t true values and executor isn`t null then shutdown it
     */
    private void checkAsync() {
        if (!asyncTable.values().contains(true) && executor != null) {
            executor.shutdown();
            executor = null;
        }
    }

    /**
     * Call {@link IEvent JNet event} for each handler with that registered event
     * <p>
     * Check all handlers for it has registered event, if it has that then check if his async
     * Is async - run in {@link #executor}, else - run sync
     *
     * @param event - target event
     */
    public void callEvent(IEvent event) {
        Class<? extends IEvent> clazz = event.getClass();
        for (Map.Entry<IEventHandler, Integer> entry : handlers.entrySet()) {
            int id = entry.getValue();
            if (!methodTable.containsRow(id) && !methodTable.containsColumn(clazz)) {
                continue;
            }
            boolean async = Boolean.TRUE.equals(asyncTable.get(id, clazz));
            if (async) {
                executor.submit(() -> Objects.requireNonNull(methodTable.get(id, clazz)).invoke(entry.getKey(), entry));
            } else {
                try {
                    Objects.requireNonNull(methodTable.get(id, clazz)).invoke(entry.getKey(), event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NullPointerException ignored) {}
            }
        }
    }

    /**
     * Unregister all handlers and tables
     * Shutdown executor if it`s registered
     */
    public void close() {
        handlers.clear();
        methodTable.clear();
        asyncTable.clear();

        if (executor != null) {
            executor.shutdown();
            executor = null;
        }
    }
}