package io.madipalli.prabhu.SpringWithReact.managers.internal.mappers;

import io.madipalli.prabhu.SpringWithReact.managers.dto.PositionCreationRequestDto;
import io.madipalli.prabhu.SpringWithReact.managers.dto.PositionManagerDto;
import io.madipalli.prabhu.SpringWithReact.dal.entites.PositionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PositionMapper {
    PositionMapper INSTANCE = Mappers.getMapper(PositionMapper.class);
    PositionEntity convert(PositionCreationRequestDto dto);
    PositionManagerDto convert(PositionEntity entity);
}
