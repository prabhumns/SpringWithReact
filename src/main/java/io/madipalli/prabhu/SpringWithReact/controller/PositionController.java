package io.madipalli.prabhu.SpringWithReact.controller;

import io.madipalli.prabhu.SpringWithReact.controller.dto.CreatePositionRequest;
import io.madipalli.prabhu.SpringWithReact.controller.dto.PositionResponse;
import io.madipalli.prabhu.SpringWithReact.controller.internal.PositionMapper;
import io.madipalli.prabhu.SpringWithReact.service.PositionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Behaviour object in Domain Data Layer. <a href="https://w.amazon.com/bin/view/ACBDA_Pattern/Specifications/2.0">ACBDA Pattern</a>
 */
@RestController
@RequestMapping("/api/v1/positions")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PositionController {

	private final PositionService positionService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PositionResponse createPosition(
		@RequestBody final CreatePositionRequest positionRequest
	) {
		return PositionMapper.INSTANCE.convertPositionEntityToPositionResponse(
			positionService.createPosition(positionRequest.getName())
		);
	}

	@GetMapping("/{positionId}")
	@ResponseStatus(HttpStatus.OK)
	public PositionResponse findSinglePositionWithPositionId(
		@PathVariable final String positionId
	) {
		final var bo = positionService.findPositionById(positionId);
		return PositionMapper.INSTANCE.convertPositionEntityToPositionResponse(
			bo
		);
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<PositionResponse> findAllPositions() {
		final var bos = positionService.findAllPositions();
		return PositionMapper.INSTANCE.convertPositionEntityToPositionResponse(
			bos
		);
	}

	@DeleteMapping
	public void deleteAllPosition() {
		positionService.deleteAllPosition();
	}
}
