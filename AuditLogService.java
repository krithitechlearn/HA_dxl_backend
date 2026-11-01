package com.ha.dxl.audit.service;

/**
 * Service for recording immutable audit log entries.
 * @see US-011
 */
public interface AuditLogService {

    /**
     * Records an audit log entry for a significant system action.
     *
     * @param actor      the username performing the action
     * @param action     the action performed (e.g., CREATE, UPDATE, DELETE)
     * @param entityType the type of entity affected
     * @param entityId   the ID of the entity affected
     * @param before     the state before the action (may be null)
     * @param after      the state after the action (may be null)
     * @param ipAddress  the IP address of the requester (may be null)
     */
    void logAction(String actor, String action, String entityType, String entityId,
                   Object before, Object after, String ipAddress);
}
