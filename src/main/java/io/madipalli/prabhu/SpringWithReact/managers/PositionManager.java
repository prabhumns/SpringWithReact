package io.madipalli.prabhu.SpringWithReact.managers;

import io.madipalli.prabhu.SpringWithReact.dal.sql.PositionSqlRepository;
import io.madipalli.prabhu.SpringWithReact.managers.dto.PositionCreationRequestDto;
import io.madipalli.prabhu.SpringWithReact.managers.dto.PositionManagerDto;
import io.madipalli.prabhu.SpringWithReact.managers.internal.mappers.PositionMapper;
import io.madipalli.prabhu.SpringWithReact.dal.mongo.PositionMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Behaviour object in Domain Data Layer. <a href="https://w.amazon.com/bin/view/ACBDA_Pattern/Specifications/2.0">ACBDA Pattern</a>
 */
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PositionManager {
    private final PositionMongoRepository mongoRepository;
    private final PositionSqlRepository sqlRepository;

    @Transactional
    public PositionManagerDto createPositionWithName(final PositionCreationRequestDto requestDto) {
        final var current = Instant.now();
        log.info("Creation time is {}", current);
        final var basePosition = PositionMapper.INSTANCE.convert(requestDto)
                .toBuilder()
                .updateTime(current)
                .creationTime(current)
                .build();
        final var positionEntity = sqlRepository.save(basePosition);
        log.info("Saved in sql {}", positionEntity);
        final var mongoSavedEntity = mongoRepository.save(positionEntity);
        log.info("Saved in mongo {}", mongoSavedEntity);
        return PositionMapper.INSTANCE.convert(mongoSavedEntity);
    }
}
