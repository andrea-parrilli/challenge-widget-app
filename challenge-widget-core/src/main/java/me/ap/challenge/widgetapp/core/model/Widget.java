package me.ap.challenge.widgetapp.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor(
        onConstructor = @__(@JsonCreator)
)
@NoArgsConstructor
public class Widget {
    private Long id;
    @NotNull
    private Integer width;
    @NotNull
    private Integer height;
    private Integer z;
}
