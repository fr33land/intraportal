package org.intraportal.persistence.repository.audit;

import org.intraportal.persistence.model.audit.AuditAction;
import org.intraportal.persistence.model.audit.AuditDomain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ActionAudit {

    AuditDomain domain();

    AuditAction action();

}
