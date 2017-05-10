package com.ofs.server.form.update;

import java.util.Objects;

public class PropertyChange {
    private String property;
    private Object oldValue;
    private Object newValue;


    private PropertyChange(String property, Object oldValue, Object newValue)
    {
        this.property = property;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getProperty()
    {
        return property;
    }

    public <T> T getOldValue(Class<T> type)
    {
        return type.cast(oldValue);
    }

    public <T> T getNewValue(Class<T> type)
    {
        return type.cast(newValue);
    }


    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        PropertyChange that = (PropertyChange) o;
        return Objects.equals(property, that.property) &&
                Objects.equals(oldValue, that.oldValue) &&
                Objects.equals(newValue, that.newValue);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(property, oldValue, newValue);
    }

    @Override
    public String toString()
    {
        return "PropertyChange{" +
                "property='" + property + '\'' +
                ", oldValue=" + oldValue +
                ", newValue=" + newValue +
                '}';
    }


    public static PropertyChange create(String propertyName, Object oldValue, Object newValue)
    {
        return new PropertyChange(propertyName, oldValue, newValue);
    }
}
