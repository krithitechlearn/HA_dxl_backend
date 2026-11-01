package com.ha.dxl.device.domain;

/**
 * Enumeration of device command execution statuses.
 * @see US-006
 */
public enum CommandStatus {
    PENDING,
    SENT,
    ACKNOWLEDGED,
    FAILED
}
