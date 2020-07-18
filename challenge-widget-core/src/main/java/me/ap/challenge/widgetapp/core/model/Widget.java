package me.ap.challenge.widgetapp.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor(
        onConstructor = @__(@JsonCreator)
)
@NoArgsConstructor
public class Widget {
    private Long id;
    private Integer width;
    private Integer height;
    private Integer z;
}
