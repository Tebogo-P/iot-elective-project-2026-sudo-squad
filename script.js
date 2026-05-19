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

async function fetchStudents() {

    const tableBody =
        document.getElementById("studentsTableBody");

    try {

        const response = await fetch("http://localhost:8080/api/v1/students/enrolled");

        if (!response.ok) {
            throw new Error("Server Error");
        }

        const students = await response.json();

        tableBody.innerHTML = "";

        if (students.length === 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="7" style="text-align: center; color: #999;">
                        No enrolled students found.
                    </td>
                </tr>
            `;
            return;
        }

        students.forEach(student => {

            const row = `
                <tr>
                    <td>${student.firstName}</td>
                    <td>${student.lastName}</td>
                    <td>${student.studentNumber}</td>
                    <td>${student.email}</td>
                    <td>${student.rfidTagId || "N/A"}</td>
                    <td>${student.fingerprintId || "N/A"}</td>
                    <td><span class="status-badge enrolled">Enrolled</span></td>
                </tr>
            `;

            tableBody.innerHTML += row;
        });

    } catch (error) {

        console.error(error);

        tableBody.innerHTML = `
            <tr>
                <td colspan="7" style="text-align: center; color: #999;">
                    Unable to load students.
                </td>
            </tr>
        `;
    }
}

// Load immediately
fetchAttendanceLogs();
fetchStudents();

// Refresh every 5 seconds
setInterval(fetchAttendanceLogs, 5000);
setInterval(fetchStudents, 10000);