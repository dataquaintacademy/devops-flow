package com.devops.app.model;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * Domain entity representing a build/deployment record executed by the CI/CD pipeline.
 */
public class DeploymentRecord {

    private String id;

    @NotBlank(message = "Service name is required")
    private String serviceName;

    @NotBlank(message = "Build version is required")
    private String version;

    private String environment; // e.g. QA, Staging, Production
    private String status;      // e.g. SUCCESS, FAILED, IN_PROGRESS
    private String commitHash;
    private String deployedBy;  // e.g. Jenkins, User
    private LocalDateTime timestamp;
    private long durationMillis;

    public DeploymentRecord() {
        this.timestamp = LocalDateTime.now();
    }

    public DeploymentRecord(String id, String serviceName, String version, String environment,
                            String status, String commitHash, String deployedBy, long durationMillis) {
        this.id = id;
        this.serviceName = serviceName;
        this.version = version;
        this.environment = environment;
        this.status = status;
        this.commitHash = commitHash;
        this.deployedBy = deployedBy;
        this.timestamp = LocalDateTime.now();
        this.durationMillis = durationMillis;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCommitHash() {
        return commitHash;
    }

    public void setCommitHash(String commitHash) {
        this.commitHash = commitHash;
    }

    public String getDeployedBy() {
        return deployedBy;
    }

    public void setDeployedBy(String deployedBy) {
        this.deployedBy = deployedBy;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    public void setDurationMillis(long durationMillis) {
        this.durationMillis = durationMillis;
    }
}
