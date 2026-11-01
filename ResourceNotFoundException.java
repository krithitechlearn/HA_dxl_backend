package com.ha.dxl.exception;

import lombok.Getter;

/**
 * Exception thrown when a requested resource is not found.
 * Maps to HTTP 404.
 */
@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceType;
    private final String resourceId;

    public ResourceNotFoundException(String resourceType, String resourceId) {
        super(resourceType + " not found with id: " + resourceId);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }
}
