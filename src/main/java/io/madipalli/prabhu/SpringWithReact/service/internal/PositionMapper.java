package io.madipalli.prabhu.SpringWithReact.service.internal;

import io.madipalli.prabhu.SpringWithReact.managers.dto.PositionManagerDto;
import io.madipalli.prabhu.SpringWithReact.service.dtos.PositionServiceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Behaviour object in Domain Data Layer. <a href="https://w.amazon.com/bin/view/ACBDA_Pattern/Specifications/2.0">ACBDA Pattern</a>
 */
@Mapper
public interface PositionMapper {
    PositionMapper INSTANCE = Mappers.getMapper(PositionMapper.class);
    PositionServiceDto convert(PositionManagerDto dto);
}
