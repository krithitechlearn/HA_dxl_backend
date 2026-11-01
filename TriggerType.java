package com.ha.dxl.automation.domain;

/**
 * Enumeration of automation rule trigger types.
 * @see US-009, US-010, A-07
 */
public enum TriggerType {
    SCHEDULE,
    DEVICE_STATE,
    SUNRISE,
    SUNSET,
    MANUAL
}
