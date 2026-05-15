const API_URL = "http://localhost:8080/api/v1/attendance/logs";

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
            if (log.status === "PRESENT") {
                presentToday++;
            }

            if (log.status === "DENIED") {
                accessDenied++;
            }

            // Status styling
            let statusClass = "";

            if (log.status === "PRESENT") {
                statusClass = "present";
            }
            else if (log.status === "LATE") {
                statusClass = "late";
            }
            else {
                statusClass = "denied";
            }

            // Create row
            const row = `
                <tr>
                    <td>${log.name}</td>
                    <td>${log.studentNumber}</td>
                    <td>${log.timestamp}</td>
                    <td class="${statusClass}">
                        ${log.status}
                    </td>
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