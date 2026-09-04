// DevOpsFlow Client Logic
document.addEventListener('DOMContentLoaded', () => {
    initApp();
});

function initApp() {
    loadHealthTelemetry();
    loadServices();
    loadDeployments();

    // Auto-refresh telemetry every 5 seconds
    setInterval(loadHealthTelemetry, 5000);

    // Event listeners
    const btnRefresh = document.getElementById('btnRefreshServices');
    if (btnRefresh) {
        btnRefresh.addEventListener('click', () => {
            loadServices();
            loadHealthTelemetry();
        });
    }

    const btnDeploy = document.getElementById('btnTriggerDeploy');
    if (btnDeploy) {
        btnDeploy.addEventListener('click', triggerSimulatedDeployment);
    }
}

// Format seconds into human readable duration
function formatUptime(seconds) {
    if (seconds < 60) return `${seconds}s`;
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    if (mins < 60) return `${mins}m ${secs}s`;
    const hours = Math.floor(mins / 60);
    return `${hours}h ${mins % 60}m`;
}

// 1. Fetch Health & System Telemetry
async function loadHealthTelemetry() {
    try {
        const response = await fetch('/api/health');
        if (!response.ok) throw new Error('Health check failed');
        const data = await response.json();

        // Update DOM metrics
        const metricStatus = document.getElementById('metricStatus');
        const metricUptime = document.getElementById('metricUptime');
        const metricMemory = document.getElementById('metricMemory');
        const metricMemoryTotal = document.getElementById('metricMemoryTotal');
        const metricSuccessRate = document.getElementById('metricSuccessRate');
        const metricDeployCount = document.getElementById('metricDeployCount');
        const metricJavaVer = document.getElementById('metricJavaVer');

        if (metricStatus) metricStatus.textContent = data.status || 'UP';
        if (metricUptime) metricUptime.textContent = formatUptime(data.uptimeSeconds || 0);
        if (metricMemory) metricMemory.textContent = `${data.usedMemoryMB || 0} MB`;
        if (metricMemoryTotal) metricMemoryTotal.textContent = `Max: ${data.maxMemoryMB || 0} MB | Allocated: ${data.totalMemoryMB || 0} MB`;
        if (metricSuccessRate) metricSuccessRate.textContent = `${data.successRatePercent || 100}%`;
        if (metricDeployCount) metricDeployCount.textContent = `${data.totalDeployments || 0} Deployments`;
        if (metricJavaVer) metricJavaVer.textContent = `${data.javaVersion || 'Java 21'} (${data.osName || 'Host'})`;
    } catch (err) {
        console.warn('Telemetry load warning:', err.message);
    }
}

// 2. Fetch Infrastructure Services
async function loadServices() {
    const listEl = document.getElementById('servicesList');
    if (!listEl) return;

    try {
        const response = await fetch('/api/services');
        if (!response.ok) throw new Error('Failed to load services');
        const services = await response.json();

        if (services.length === 0) {
            listEl.innerHTML = '<div class="text-muted">No monitored services found.</div>';
            return;
        }

        listEl.innerHTML = services.map(srv => `
            <div class="service-item">
                <div class="service-info">
                    <span class="service-name">${srv.name}</span>
                    <span class="service-host">${srv.host}:${srv.port} &bull; v${srv.version}</span>
                </div>
                <div class="service-meta">
                    <span class="badge badge-status-success">${srv.status}</span>
                    <div class="service-latency">${srv.responseTime || '< 20ms'}</div>
                </div>
            </div>
        `).join('');
    } catch (err) {
        listEl.innerHTML = `<div class="text-danger">Error loading services: ${err.message}</div>`;
    }
}

// 3. Fetch Deployments
async function loadDeployments() {
    const tbody = document.getElementById('deploymentsTableBody');
    if (!tbody) return;

    try {
        const response = await fetch('/api/deployments');
        if (!response.ok) throw new Error('Failed to load deployments');
        const deployments = await response.json();

        if (deployments.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center">No deployments recorded.</td></tr>';
            return;
        }

        tbody.innerHTML = deployments.map(dep => `
            <tr>
                <td><strong>${dep.id}</strong></td>
                <td>v${dep.version}</td>
                <td><span class="badge badge-outline">${dep.environment || 'Production'}</span></td>
                <td><code>${dep.commitHash ? dep.commitHash.substring(0, 7) : 'HEAD'}</code></td>
                <td><span class="badge badge-status-success">${dep.status}</span></td>
            </tr>
        `).join('');
    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="5" class="text-danger">Error loading deployments: ${err.message}</td></tr>`;
    }
}

// 4. Trigger Simulated Deployment
async function triggerSimulatedDeployment() {
    const randomHash = Math.random().toString(36).substring(2, 9);
    const newVersion = `1.0.${Math.floor(Math.random() * 90) + 10}`;

    const payload = {
        serviceName: 'DevOps-Flow-App',
        version: newVersion,
        environment: 'Production',
        status: 'SUCCESS',
        commitHash: randomHash,
        deployedBy: 'Jenkins-CI-CD',
        durationMillis: Math.floor(Math.random() * 20000) + 25000
    };

    try {
        const response = await fetch('/api/deployments', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            await loadDeployments();
            await loadHealthTelemetry();
        }
    } catch (err) {
        console.error('Failed to trigger deployment:', err);
    }
}
