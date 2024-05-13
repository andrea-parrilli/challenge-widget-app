package me.ap.challenge.widgetapp.core.deserialize;

public interface Buildable<T> {
    default T build() {
        return null;
    }
}
