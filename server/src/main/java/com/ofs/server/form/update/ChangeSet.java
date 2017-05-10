package com.ofs.server.form.update;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Created by mpenna on 3/31/17.
 */
public class ChangeSet implements Iterable {

    private Map<String,PropertyChange> changes;

    private ChangeSet(Map<String,PropertyChange> changes)
    {
        this.changes = changes;
    }


    // TODO Property names can span sub-objects.
    // Should I provide some sort of functionality to sub divide the change set based on object depth?
    // Maybe the PropertyChange should have some sort of flag indicating object vs property


    /**
     * Returns a PropertyChange for a given property by name or {@code null}
     * if the named property was not changed.
     */
    public PropertyChange find(String property)
    {
        return changes.get(property);
    }

    /**
     * Returns {@code true} if this collection contains a change associated with
     * the named property.
     */
    public boolean contains(String property)
    {
        return changes.containsKey(property);
    }

    /**
     * Returns {@code true} if any of the specified properties are contained in
     * this change set, {@code false} otherwise.
     */
    public boolean containsAny(String ... properties)
    {
        for(String property : properties) {
            if(changes.containsKey(property)) return true;
        }
        return false;
    }

    /**
     * Returns a list of property names that were changed.
     */
    public Set<String> properties()
    {
        return Collections.unmodifiableSet(changes.keySet());
    }

    /**
     * Returns the number of changes this container encapsulates.
     */
    public int size()
    {
        return changes.size();
    }

    /**
     * Returns a Stream over the collection of PropertyChanges
     */
    public Stream<PropertyChange> stream()
    {
        return changes.values().stream();
    }

    /**
     * Returns an Iterator over the collection of PropertyChanges
     */
    @Override
    public Iterator<PropertyChange> iterator()
    {
        return changes.values().iterator();
    }


    static Builder builder()
    {
        return new Builder();
    }


    /**
     * So I envision the most common use cases for change sets to be one of the following
     *
     * 1) If property X was changed fire a specific event: for example status
     * 2) If property Y was changed check property Z was also changed
     * 3) If property A was changed throw security exception
     * 4) Iterate list of changes for an outbound change log/event/message
     * 5) Given named property, apply some validation rule to new value
     * 6) Possibly used for database update (rather than updating entire object)
     */

    static class Builder {

        private Map<String,PropertyChange> changes = new LinkedHashMap<>();

        public Builder add(String property, Object oldValue, Object newValue)
        {
            changes.put(property, PropertyChange.create(property, oldValue, newValue));
            return this;
        }

        public ChangeSet build()
        {
            return new ChangeSet(Collections.unmodifiableMap(changes));
        }

    }
}
