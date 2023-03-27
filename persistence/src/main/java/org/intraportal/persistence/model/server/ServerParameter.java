package org.intraportal.persistence.model.server;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.intraportal.persistence.model.audit.AuditMetadata;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "server_parameters")
public class ServerParameter extends AuditMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "param_name", nullable = false)
    private String paramName;

    @Size(max = 255)
    @NotNull
    @Column(name = "param_value", nullable = false)
    private String paramValue;

    @Size(max = 255)
    @NotNull
    @Column(name = "param_type", nullable = false)
    private String paramType;

    @Size(max = 255)
    @NotNull
    @Column(name = "param_group", nullable = false)
    private String paramGroup;

}