package com.ofs.server.form.update;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class ChangeSet implements Iterable {

    private Map<String,PropertyChange> changes;

    private ChangeSet(Map<String,PropertyChange> changes)
    {
        this.changes = changes;
    }

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
