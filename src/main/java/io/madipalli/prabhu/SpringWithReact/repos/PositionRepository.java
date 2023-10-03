package io.madipalli.prabhu.SpringWithReact.repos;

import io.madipalli.prabhu.SpringWithReact.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Behaviour object in Domain Data Layer. <a href="https://w.amazon.com/bin/view/ACBDA_Pattern/Specifications/2.0">ACBDA Pattern</a>
 */
public interface PositionRepository extends JpaRepository<Position, String> {
}
