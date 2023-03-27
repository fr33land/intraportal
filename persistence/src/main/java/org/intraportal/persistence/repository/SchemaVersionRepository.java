package org.intraportal.persistence.repository;

import org.intraportal.persistence.model.MigrationRecord;
import org.springframework.stereotype.Repository;

@Repository("SchemaVersionRepository")
public interface SchemaVersionRepository extends org.springframework.data.repository.Repository<MigrationRecord, Integer> {

    MigrationRecord findTopByOrderByInstalledRankDesc();

}