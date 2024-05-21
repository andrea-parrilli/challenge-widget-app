package me.ap.challenge.widgetapp.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

@Entity
@Accessors(fluent = true)
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Widget {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    Long id;
    @NotNull Integer width;
    @NotNull Integer height;
    @NotNull
    Integer z;
}
