package io.madipalli.prabhu.SpringWithReact.service;

import io.madipalli.prabhu.SpringWithReact.entity.Position;
import io.madipalli.prabhu.SpringWithReact.repos.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Behaviour object in Domain Data Layer. <a href="https://w.amazon.com/bin/view/ACBDA_Pattern/Specifications/2.0">ACBDA Pattern</a>
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class PositionService {
    private final PositionRepository repository;

    public Position createPosition(final String positionName){
        final var position = Position.builder()
                .creationTime(Instant.now())
                .updateTime(Instant.now())
                .name(positionName)
                .build();
        return  repository.save(position);
    }
    public Position findPositionById(final String positionId){
        return repository.findById(positionId).orElseThrow();
    }

    public List<Position> findAllPositions(){
        return repository.findAll();
    }
}
