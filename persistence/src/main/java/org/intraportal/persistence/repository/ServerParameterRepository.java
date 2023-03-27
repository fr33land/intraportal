package org.intraportal.persistence.repository;

import org.intraportal.persistence.model.server.ServerParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ServerParameterRepository")
public interface ServerParameterRepository extends JpaRepository<ServerParameter, Integer> {

    List<ServerParameter> findByParamGroup(String paramGroup);

    ServerParameter findByParamNameAndParamGroup(String paramName, String paramGroup);
}
