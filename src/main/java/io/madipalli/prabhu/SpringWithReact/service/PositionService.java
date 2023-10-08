package io.madipalli.prabhu.SpringWithReact.service;

import io.madipalli.prabhu.SpringWithReact.dal.sql.PositionSqlRepository;
import io.madipalli.prabhu.SpringWithReact.managers.dto.PositionCreationRequestDto;
import io.madipalli.prabhu.SpringWithReact.dal.entites.PositionEntity;
import io.madipalli.prabhu.SpringWithReact.managers.PositionManager;
import io.madipalli.prabhu.SpringWithReact.service.dtos.PositionServiceDto;
import io.madipalli.prabhu.SpringWithReact.service.internal.PositionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Behaviour object in Domain Data Layer. <a href="https://w.amazon.com/bin/view/ACBDA_Pattern/Specifications/2.0">ACBDA Pattern</a>
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
@Slf4j
public class PositionService {
    private final PositionManager manager;
    private final PositionSqlRepository repository;

    @Transactional
    public PositionServiceDto createPosition(final String positionName) {
        final var position = manager.createPositionWithName(PositionCreationRequestDto.builder()
                .name(positionName)
                .build());
        return PositionMapper.INSTANCE.convert(position);
    }

    public PositionEntity findPositionById(final String positionId) {
        return repository.findById(positionId).orElseThrow();
    }

    public List<PositionEntity> findAllPositions() {
        return repository.findAll();
    }
}
