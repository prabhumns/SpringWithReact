package io.madipalli.prabhu.SpringWithReact.service.dtos;

import java.time.Instant;
import lombok.Builder;
import lombok.Value;

/**
 * Behaviour object in Domain Data Layer. <a href="https://w.amazon.com/bin/view/ACBDA_Pattern/Specifications/2.0">ACBDA Pattern</a>
 */
@Value
@Builder(toBuilder = true)
public class PositionServiceDto {

	String positionId;
	Instant creationTime;
	Instant updateTime;
	String name;
}
