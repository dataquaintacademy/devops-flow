package com.devops.app.service;

import com.devops.app.model.DeploymentRecord;
import com.devops.app.model.ServiceStatus;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Service managing deployments, system telemetry, and pipeline health records.
 */
@Service
public class DeploymentService {

    private final List<DeploymentRecord> deployments = new CopyOnWriteArrayList<>();
    private final Map<String, ServiceStatus> serviceRegistry = new ConcurrentHashMap<>();
    private final long applicationStartTime = System.currentTimeMillis();

    public DeploymentService() {
        seedInitialData();
    }

    private void seedInitialData() {
        // Seed pipeline services
        registerService(new ServiceStatus("DevOps-Flow-App", "1.0.0-SNAPSHOT", "UP", "tomcat-prod-01", 8080, 14200, "12ms"));
        registerService(new ServiceStatus("Jenkins-CI-CD", "2.440.3", "UP", "jenkins-master", 8080, 86400, "45ms"));
        registerService(new ServiceStatus("Nexus-Repository", "3.68.0", "UP", "nexus-repo-01", 8081, 72000, "28ms"));
        registerService(new ServiceStatus("SonarQube-Server", "10.5.1", "UP", "sonar-host-01", 9000, 93000, "32ms"));

        // Seed recent pipeline deployments
        deployments.add(new DeploymentRecord("DEP-1001", "DevOps-Flow-App", "0.9.8", "QA", "SUCCESS", "a78ef3c", "Jenkins-CI", 45000));
        deployments.add(new DeploymentRecord("DEP-1002", "DevOps-Flow-App", "0.9.9", "Staging", "SUCCESS", "b42ce91", "Jenkins-CI", 52000));
        deployments.add(new DeploymentRecord("DEP-1003", "DevOps-Flow-App", "1.0.0-SNAPSHOT", "Production", "SUCCESS", "f982da4", "Jenkins-CI", 48500));
    }

    public List<DeploymentRecord> getAllDeployments() {
        return new ArrayList<>(deployments);
    }

    public Optional<DeploymentRecord> getDeploymentById(String id) {
        return deployments.stream().filter(d -> d.getId().equalsIgnoreCase(id)).findFirst();
    }

    public DeploymentRecord recordDeployment(DeploymentRecord record) {
        if (record.getId() == null || record.getId().isBlank()) {
            record.setId("DEP-" + (1000 + deployments.size() + 1));
        }
        if (record.getTimestamp() == null) {
            record.setTimestamp(LocalDateTime.now());
        }
        deployments.add(0, record); // prepend newest
        return record;
    }

    public Collection<ServiceStatus> getAllServices() {
        return serviceRegistry.values();
    }

    public ServiceStatus registerService(ServiceStatus status) {
        serviceRegistry.put(status.getName(), status);
        return status;
    }

    public Map<String, Object> getSystemTelemetry() {
        Runtime runtime = Runtime.getRuntime();
        RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();

        long uptimeMs = System.currentTimeMillis() - applicationStartTime;
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();

        Map<String, Object> telemetry = new HashMap<>();
        telemetry.put("appName", "DevOpsFlow Web Application");
        telemetry.put("version", "1.0.0-SNAPSHOT");
        telemetry.put("status", "UP");
        telemetry.put("packaging", "WAR (Web Application Archive)");
        telemetry.put("targetServer", "Apache Tomcat");
        telemetry.put("uptimeSeconds", uptimeMs / 1000);
        telemetry.put("jvmUptimeMs", mxBean.getUptime());
        telemetry.put("availableProcessors", runtime.availableProcessors());
        telemetry.put("usedMemoryMB", usedMemory / (1024 * 1024));
        telemetry.put("totalMemoryMB", totalMemory / (1024 * 1024));
        telemetry.put("maxMemoryMB", maxMemory / (1024 * 1024));
        telemetry.put("javaVersion", System.getProperty("java.version"));
        telemetry.put("osName", System.getProperty("os.name"));
        telemetry.put("osArch", System.getProperty("os.arch"));
        telemetry.put("pipelineStages", List.of(
                "1. Git (Code Commit)",
                "2. GitHub (Push Code)",
                "3. Maven (Build & Unit Tests)",
                "4. SonarQube (Static Analysis & Quality Gate)",
                "5. Trivy (Security Vulnerability Scan)",
                "6. Nexus (Artifact Repository)",
                "7. Tomcat (Deploy *.war)",
                "8. Monitor (Live App Telemetry)"
        ));

        long totalDeployments = deployments.size();
        long successfulDeployments = deployments.stream().filter(d -> "SUCCESS".equalsIgnoreCase(d.getStatus())).count();
        double successRate = totalDeployments > 0 ? ((double) successfulDeployments / totalDeployments) * 100 : 100.0;

        telemetry.put("totalDeployments", totalDeployments);
        telemetry.put("successRatePercent", Math.round(successRate * 10.0) / 10.0);
        telemetry.put("activeServicesCount", serviceRegistry.size());

        return telemetry;
    }
}
