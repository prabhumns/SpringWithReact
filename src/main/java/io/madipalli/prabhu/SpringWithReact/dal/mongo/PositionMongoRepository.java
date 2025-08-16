package io.madipalli.prabhu.SpringWithReact.dal.mongo;

import io.madipalli.prabhu.SpringWithReact.dal.entites.PositionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PositionMongoRepository
	extends MongoRepository<PositionEntity, String> {}
