package edu.wpi.grip.core;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import edu.wpi.grip.core.events.OperationAddedEvent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The palette is a library of operations that can be added as steps in the {@link Pipeline}
 */
@Singleton
public class Palette {

    private final EventBus eventBus;

    @Inject
    Palette(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    private final Map<String, OperationDescription> operations = new LinkedHashMap<>();

    @Subscribe
    public void onOperationAdded(OperationAddedEvent event) {
        final OperationDescription<?> operation = event.getOperation();
        map(operation.getName(), operation);
        for(String alias : operation.getAliases()) {
            map(alias, operation);
        }
    }

    /**
     * Maps the key to the given operation
     * @param key The key the operation should be mapped to
     * @param operation The operation to map the key to
     * @throws IllegalArgumentException if the key is already in the {@link #operations} map.
     */
    private void map(String key, OperationDescription operation) {
        checkArgument(!operations.containsKey(key), "Operation name or alias already exists: " + key);
        operations.put(key, operation);
    }

    /**
     * @return A collection of all available operations
     */
    public Collection<OperationDescription> getOperations() {
        return this.operations.values();
    }

    /**
     * @return The operation with the specified unique name
     */
    public Optional<OperationDescription> getOperationByName(String name) {
        return Optional.ofNullable(this.operations.get(checkNotNull(name, "name cannot be null")));
    }
}
