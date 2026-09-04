package com.devops.app.controller;

import com.devops.app.model.DeploymentRecord;
import com.devops.app.service.DeploymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiController.class)
@DisplayName("ApiController Integration Tests")
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DeploymentService deploymentService;

    @Test
    @DisplayName("GET /api/health should return 200 and telemetry data")
    void testGetHealth() throws Exception {
        Map<String, Object> mockTelemetry = Map.of(
                "status", "UP",
                "appName", "DevOpsFlow Web Application",
                "packaging", "WAR (Web Application Archive)",
                "targetServer", "Apache Tomcat"
        );
        when(deploymentService.getSystemTelemetry()).thenReturn(mockTelemetry);

        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.packaging").value("WAR (Web Application Archive)"))
                .andExpect(jsonPath("$.targetServer").value("Apache Tomcat"));
    }

    @Test
    @DisplayName("GET /api/deployments should return list of deployments")
    void testGetDeployments() throws Exception {
        DeploymentRecord record = new DeploymentRecord("DEP-1001", "DevOps-Flow-App", "1.0.0",
                "Production", "SUCCESS", "abc1234", "Jenkins", 45000);
        when(deploymentService.getAllDeployments()).thenReturn(List.of(record));

        mockMvc.perform(get("/api/deployments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("DEP-1001"))
                .andExpect(jsonPath("$[0].serviceName").value("DevOps-Flow-App"))
                .andExpect(jsonPath("$[0].status").value("SUCCESS"));
    }

    @Test
    @DisplayName("POST /api/deployments should create new deployment and return 201")
    void testCreateDeployment_Valid() throws Exception {
        DeploymentRecord input = new DeploymentRecord(null, "DevOps-Flow-App", "1.0.1",
                "Production", "SUCCESS", "def5678", "Jenkins-Pipeline", 32000);

        DeploymentRecord saved = new DeploymentRecord("DEP-1004", "DevOps-Flow-App", "1.0.1",
                "Production", "SUCCESS", "def5678", "Jenkins-Pipeline", 32000);

        when(deploymentService.recordDeployment(any(DeploymentRecord.class))).thenReturn(saved);

        mockMvc.perform(post("/api/deployments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("DEP-1004"))
                .andExpect(jsonPath("$.version").value("1.0.1"));
    }

    @Test
    @DisplayName("POST /api/deployments with blank serviceName should return 400 Bad Request")
    void testCreateDeployment_Invalid() throws Exception {
        DeploymentRecord invalidInput = new DeploymentRecord();
        invalidInput.setServiceName(""); // Blank triggers validation error
        invalidInput.setVersion("");

        mockMvc.perform(post("/api/deployments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInput)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/pipeline/info should return 8 pipeline stages matching the architecture")
    void testGetPipelineInfo() throws Exception {
        mockMvc.perform(get("/api/pipeline/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orchestrator").value("Jenkins"))
                .andExpect(jsonPath("$.artifact").value("devops-flow.war"))
                .andExpect(jsonPath("$.stages.length()").value(8));
    }
}
