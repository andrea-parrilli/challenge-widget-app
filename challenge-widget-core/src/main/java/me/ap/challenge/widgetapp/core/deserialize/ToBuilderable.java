package me.ap.challenge.widgetapp.core.deserialize;

public interface ToBuilderable<T> {
    default T toBuilder() {
        return null;
    }
}
