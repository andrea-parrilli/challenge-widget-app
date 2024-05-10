package me.ap.challenge.widgetapp.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder(toBuilder = true)
public record Fluffer(int num, String str) {
}
