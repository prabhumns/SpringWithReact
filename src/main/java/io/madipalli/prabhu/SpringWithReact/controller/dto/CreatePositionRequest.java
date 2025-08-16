package io.madipalli.prabhu.SpringWithReact.controller.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Behaviour object in Domain Data Layer. <a href="https://w.amazon.com/bin/view/ACBDA_Pattern/Specifications/2.0">ACBDA Pattern</a>
 */
@Value
@Builder
@Jacksonized
public class CreatePositionRequest {

	String name;
}
