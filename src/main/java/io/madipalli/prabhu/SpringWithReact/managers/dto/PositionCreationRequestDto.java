package io.madipalli.prabhu.SpringWithReact.managers.dto;

import lombok.Builder;
import lombok.Value;

/**
 * Data object in Domain Data Layer. <a href="https://w.amazon.com/bin/view/ACBDA_Pattern/Specifications/2.0">ACBDA Pattern</a>
 */
@Value
@Builder(toBuilder = true)
public class PositionCreationRequestDto {

	String name;
}
