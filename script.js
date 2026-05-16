const API_URL = "http://localhost:8083/api/attendance/logs";

async function fetchAttendanceLogs() {

    const tableBody =
        document.getElementById("attendanceTableBody");

    const serverStatus =
        document.getElementById("serverStatus");

    try {

        const response = await fetch(API_URL);

        if (!response.ok) {
            throw new Error("Server Error");
        }

        const logs = await response.json();

        tableBody.innerHTML = "";

        let totalScans = logs.length;
        let presentToday = 0;
        let accessDenied = 0;

        logs.forEach(log => {

            // Count stats
            if (log.accessGranted) {
                presentToday++;
            } else {
                accessDenied++;
            }

            // Create row
            const statusText = log.accessGranted ? "PRESENT" : "DENIED";
            const statusClass = log.accessGranted ? "present" : "denied";

            const row = `
                <tr>
                    <td>${log.studentName ?? "unknown"}</td>
                    <td>${log.rfidTagId ?? "-"}</td>
                    <td>${log.scanTimestamp}</td>
                    <td class="${statusClass}">${statusText}</td>
                </tr>
            `;

            tableBody.innerHTML += row;
        });

        // Update cards
        document.getElementById("totalScans").innerHTML =
            totalScans;

        document.getElementById("presentToday").innerHTML =
            presentToday;

        document.getElementById("accessDenied").innerHTML =
            accessDenied;

        // Server online
        serverStatus.innerHTML = "ONLINE";

    } catch (error) {

        console.error(error);

        serverStatus.innerHTML = "OFFLINE";

        tableBody.innerHTML = `
            <tr>
                <td colspan="4">
                    Server Offline. Unable to load attendance logs.
                </td>
            </tr>
        `;
    }
}

// Load immediately
fetchAttendanceLogs();

// Refresh every 5 seconds
setInterval(fetchAttendanceLogs, 5000);