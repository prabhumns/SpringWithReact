package io.madipalli.prabhu.SpringWithReact.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Behaviour object in Domain Data Layer. <a href="https://w.amazon.com/bin/view/ACBDA_Pattern/Specifications/2.0">ACBDA Pattern</a>
 */
@Configuration
@EnableJpaRepositories(
	basePackages = { "io.madipalli.prabhu.SpringWithReact.dal.sql" }
)
@EnableMongoRepositories(
	basePackages = { "io.madipalli.prabhu.SpringWithReact.dal.mongo" }
)
public class RepositoryConfig {}
