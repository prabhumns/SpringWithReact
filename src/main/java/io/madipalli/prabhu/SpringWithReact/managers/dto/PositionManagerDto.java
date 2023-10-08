package io.madipalli.prabhu.SpringWithReact.managers.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

/**
 * Behaviour object in Domain Data Layer. <a href="https://w.amazon.com/bin/view/ACBDA_Pattern/Specifications/2.0">ACBDA Pattern</a>
 */
@Value
@Builder(toBuilder = true)
public class PositionManagerDto {
    String positionId;
    Instant creationTime;
    Instant updateTime;
    String name;
}
