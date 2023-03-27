package org.intraportal.persistence.repository.audit;

import org.intraportal.persistence.model.audit.ActionLog;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

@Repository("ActionLogRepository")
public interface ActionLogRepository extends DataTablesRepository<ActionLog, Integer> {

}