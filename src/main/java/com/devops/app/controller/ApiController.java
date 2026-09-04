package com.devops.app.controller;

import com.devops.app.model.DeploymentRecord;
import com.devops.app.model.ServiceStatus;
import com.devops.app.service.DeploymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * REST API controller exposing endpoints for monitoring, telemetry, and pipeline deployment events.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiController {

    private final DeploymentService deploymentService;

    public ApiController(DeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }

    /**
     * Telemetry & Health endpoint used by monitoring tools and the CI/CD pipeline.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        return ResponseEntity.ok(deploymentService.getSystemTelemetry());
    }

    /**
     * Get all recorded deployments.
     */
    @GetMapping("/deployments")
    public ResponseEntity<List<DeploymentRecord>> getDeployments() {
        return ResponseEntity.ok(deploymentService.getAllDeployments());
    }

    /**
     * Record a new deployment event (can be invoked by Jenkins post-deploy step).
     */
    @PostMapping("/deployments")
    public ResponseEntity<DeploymentRecord> createDeployment(@Valid @RequestBody DeploymentRecord record) {
        DeploymentRecord created = deploymentService.recordDeployment(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Get status of all monitored infrastructure services (Tomcat, Jenkins, Nexus, SonarQube).
     */
    @GetMapping("/services")
    public ResponseEntity<Collection<ServiceStatus>> getServices() {
        return ResponseEntity.ok(deploymentService.getAllServices());
    }

    /**
     * Get details of the 8 CI/CD pipeline stages.
     */
    @GetMapping("/pipeline/info")
    public ResponseEntity<Map<String, Object>> getPipelineInfo() {
        Map<String, Object> info = Map.of(
                "pipeline", "Enterprise CI/CD Automation",
                "orchestrator", "Jenkins",
                "artifact", "devops-flow.war",
                "stages", List.of(
                        Map.of("id", 1, "name", "Code Commit", "tool", "Git", "description", "Developer commits code locally"),
                        Map.of("id", 2, "name", "Push Code", "tool", "GitHub", "description", "Code pushed to GitHub Repository"),
                        Map.of("id", 3, "name", "Build & Test", "tool", "Maven", "description", "Build project & run unit tests (.war)"),
                        Map.of("id", 4, "name", "Code Quality", "tool", "SonarQube", "description", "Static code analysis & quality gate check"),
                        Map.of("id", 5, "name", "Security Scan", "tool", "Trivy", "description", "Scan for vulnerabilities in dependencies & filesystem"),
                        Map.of("id", 6, "name", "Artifact Repository", "tool", "Nexus", "description", "Store built artifact (.war) in Nexus"),
                        Map.of("id", 7, "name", "Deploy", "tool", "Tomcat", "description", "Deploy artifact to Apache Tomcat Server"),
                        Map.of("id", 8, "name", "Monitor", "tool", "Monitoring Dashboard", "description", "Application is live & monitored")
                )
        );
        return ResponseEntity.ok(info);
    }
}
