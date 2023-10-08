package io.madipalli.prabhu.SpringWithReact.controller.internal;

import io.madipalli.prabhu.SpringWithReact.controller.dto.PositionResponse;
import io.madipalli.prabhu.SpringWithReact.dal.entites.PositionEntity;
import io.madipalli.prabhu.SpringWithReact.service.dtos.PositionServiceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Behaviour object in Domain Data Layer. <a href="https://w.amazon.com/bin/view/ACBDA_Pattern/Specifications/2.0">ACBDA Pattern</a>
 */
@Mapper
public interface PositionMapper {
    PositionMapper INSTANCE = Mappers.getMapper(PositionMapper.class);

    PositionResponse convertPositionEntityToPositionResponse(PositionEntity entity);

    PositionResponse convertPositionEntityToPositionResponse(PositionServiceDto dto);

    List<PositionResponse> convertPositionEntityToPositionResponse(List<PositionEntity> entities);
}
