package com.ofs.server.form.update;

import static java.lang.String.format;

public class UpdatingContext<T> {
    private final ChangeSet.Builder changes;
    private final T oldEntity;
    private final T newEntity;
    private final String fieldName;

    private UpdatingContext(T oldEntity, T newEntity)
    {
        this.fieldName = null;
        this.oldEntity = oldEntity;
        this.newEntity = newEntity;
        this.changes = ChangeSet.builder();
    }

    private UpdatingContext(ChangeSet.Builder changes, String fieldName, T oldEntity, T newEntity)
    {
        this.fieldName = fieldName;
        this.oldEntity = oldEntity;
        this.newEntity = newEntity;
        this.changes = changes;
    }

    public T getOldEntity()
    {
        return oldEntity;
    }

    public T getNewEntity()
    {
        return newEntity;
    }

    public void addChange(String propertyName, Object oldValue, Object newValue)
    {
        String property = (fieldName == null) ? propertyName : format("%s.%s", fieldName, propertyName);
        changes.add(property, oldValue, newValue);
    }

    public ChangeSet changes()
    {
        return changes.build();
    }


    public UpdatingContext newFor(String propertyName, Object oldValue, Object newValue)
    {
        String property = (fieldName == null) ? propertyName : format("%s.%s", fieldName, propertyName);
        //noinspection unchecked
        return new UpdatingContext(changes, property, oldValue, newValue);
    }

    public static <T> UpdatingContext<T> create(T oldEntity, T newEntity)
    {
        return new UpdatingContext<>(oldEntity, newEntity);
    }
}
