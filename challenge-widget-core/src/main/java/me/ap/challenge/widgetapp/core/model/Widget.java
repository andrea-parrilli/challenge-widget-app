package me.ap.challenge.widgetapp.core.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Widget {
    private Long id;
    private Integer width;
    private Integer height;
    private Integer z;
}
