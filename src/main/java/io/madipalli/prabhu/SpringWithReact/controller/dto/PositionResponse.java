package io.madipalli.prabhu.SpringWithReact.controller.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

/**
 * Behaviour object in Domain Data Layer. <a href="https://w.amazon.com/bin/view/ACBDA_Pattern/Specifications/2.0">ACBDA Pattern</a>
 */
@Value
@Builder
public class PositionResponse {
    String positionId;
    Instant creationTime;
    Instant updateTime;
    String name;
}
