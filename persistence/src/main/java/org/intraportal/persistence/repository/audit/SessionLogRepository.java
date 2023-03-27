package org.intraportal.persistence.repository.audit;

import org.intraportal.persistence.model.audit.SessionLog;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

@Repository("SessionLogRepository")
public interface SessionLogRepository extends DataTablesRepository<SessionLog, Integer> {

}