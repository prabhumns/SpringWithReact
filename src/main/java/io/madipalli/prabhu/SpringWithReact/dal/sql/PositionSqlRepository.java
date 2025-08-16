package io.madipalli.prabhu.SpringWithReact.dal.sql;

import io.madipalli.prabhu.SpringWithReact.dal.entites.PositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Behaviour object in Domain Data Layer. <a href="https://w.amazon.com/bin/view/ACBDA_Pattern/Specifications/2.0">ACBDA Pattern</a>
 */
public interface PositionSqlRepository
	extends JpaRepository<PositionEntity, String> {}
