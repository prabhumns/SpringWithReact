package io.madipalli.prabhu.SpringWithReact.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

/**
 * Behaviour object in Domain Data Layer. <a href="https://w.amazon.com/bin/view/ACBDA_Pattern/Specifications/2.0">ACBDA Pattern</a>
 */
@Value
@Builder(toBuilder = true)
public class PositionResponse {
    String positionId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    Instant creationTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    Instant updateTime;
    String name;
}
