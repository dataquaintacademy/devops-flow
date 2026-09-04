package com.devops.app.model;

import java.time.LocalDateTime;

/**
 * Domain entity representing health, version, and runtime status of services monitored in the pipeline.
 */
public class ServiceStatus {

    private String name;
    private String version;
    private String status; // UP, DEGRADED, DOWN
    private String host;
    private int port;
    private long uptimeSeconds;
    private LocalDateTime lastHealthCheck;
    private String responseTime;

    public ServiceStatus() {
        this.lastHealthCheck = LocalDateTime.now();
    }

    public ServiceStatus(String name, String version, String status, String host, int port,
                         long uptimeSeconds, String responseTime) {
        this.name = name;
        this.version = version;
        this.status = status;
        this.host = host;
        this.port = port;
        this.uptimeSeconds = uptimeSeconds;
        this.responseTime = responseTime;
        this.lastHealthCheck = LocalDateTime.now();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getUptimeSeconds() {
        return uptimeSeconds;
    }

    public void setUptimeSeconds(long uptimeSeconds) {
        this.uptimeSeconds = uptimeSeconds;
    }

    public LocalDateTime getLastHealthCheck() {
        return lastHealthCheck;
    }

    public void setLastHealthCheck(LocalDateTime lastHealthCheck) {
        this.lastHealthCheck = lastHealthCheck;
    }

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }
}
