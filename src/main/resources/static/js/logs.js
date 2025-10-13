document.addEventListener("DOMContentLoaded", () => {
    const usernameBtn = document.getElementById("usernameBtn");
    const dropdown = document.getElementById("userDropdown");
    const logoutBtn = document.getElementById("logoutBtn");
    const navMenu = document.getElementById("navMenu");

    let paginaActual = 0;
    const tamañoPagina = 15;
    let totalPaginas = 0;

    function parseJwt(token) {
        try {
            const payload = token.split('.')[1];
            return JSON.parse(atob(payload));
        } catch (e) {
            return null;
        }
    }

    const token = localStorage.getItem("jwt");
    let username = null;
    let esAdmin = false;

    if (token) {
        const decoded = parseJwt(token);
        username = decoded?.sub || decoded?.username || "Usuario";
        esAdmin = decoded?.rol === "ADMIN";

        if (!esAdmin) {
            alert("No tienes permisos para acceder a esta página");
            window.location.href = "../index.html";
            return;
        }

        usernameBtn.textContent = username;
        logoutBtn.style.display = "block";

        navMenu.innerHTML = `
            <a href="vehiculos.html">Vehículos</a>
            <a href="repuestos.html">Repuestos</a>
            <a href="presupuestos.html">Presupuestos</a>
            <a href="usuarios.html">Usuarios</a>
            <a href="logs.html">Auditoría</a>
        `;
    } else {
        alert("Debes iniciar sesión");
        window.location.href = "../index.html";
        return;
    }

    usernameBtn.addEventListener("click", () => {
        dropdown.style.display = dropdown.style.display === "flex" ? "none" : "flex";
    });

    logoutBtn.addEventListener("click", () => {
        localStorage.removeItem("jwt");
        window.location.href = "formLogin.html";
    });

    document.addEventListener("click", (e) => {
        if (!usernameBtn.contains(e.target) && !dropdown.contains(e.target)) {
            dropdown.style.display = "none";
        }
    });

    const cargarLogs = (filtro = "") => {
        let url = `/logs?page=${paginaActual}&size=${tamañoPagina}`;
        if (filtro) url = `/logs/search?query=${encodeURIComponent(filtro)}&page=${paginaActual}&size=${tamañoPagina}`;

        $.ajax({
            url: url,
            method: "GET",
            headers: { "Authorization": `Bearer ${token}` },
            success: function(data) {
                const logs = data.content || data;
                const tbody = $("#logsTableBody");
                tbody.empty();

                logs.forEach(log => {
                    const detallesCortos = log.details ? (log.details.length > 50 ? log.details.substring(0,50) + '…' : log.details) : '';
                    const fila = $(`
                        <tr>
                            <td>${log.id}</td>
                            <td>${log.username}</td>
                            <td>${log.action}</td>
                            <td>${log.method}</td>
                            <td class="details-cell" title="Haz clic para ver completo">${detallesCortos}</td>
                            <td>${log.fecha ? new Date(log.fecha).toLocaleString() : ''}</td>
                        </tr>
                    `);
                    fila.find('.details-cell').css('cursor', 'pointer').on('click', () => mostrarDetalles(log.details || ''));
                    tbody.append(fila);
                });

                totalPaginas = data.totalPages || 1;
                $("#paginaActual").text(`Página ${data.number + 1} de ${totalPaginas}`);
                $("#prevBtn").prop("disabled", data.first);
                $("#nextBtn").prop("disabled", data.last);
            },
            error: function(err) {
                alert("Error al cargar logs");
                console.error(err);
            }
        });
    };

    $("#searchInput").on("input", function() {
        paginaActual = 0;
        cargarLogs($(this).val());
    });

    $("#prevBtn").click(() => {
        if (paginaActual > 0) {
            paginaActual--;
            cargarLogs($("#searchInput").val());
        }
    });

    $("#nextBtn").click(() => {
        if (paginaActual + 1 < totalPaginas) {
            paginaActual++;
            cargarLogs($("#searchInput").val());
        }
    });

    const deleteAllLogs = () => {
        if (!confirm("¿Seguro que quieres borrar todos los logs? Esta acción no se puede deshacer.")) return;

        $.ajax({
            url: "/logs/delete",
            method: "DELETE",
            headers: { "Authorization": `Bearer ${token}` },
            success: () => {
                paginaActual = 0;
                cargarLogs();
            },
            error: () => alert("Error al borrar los logs")
        });
    };

    $("#deleteAllLogsBtn").click(deleteAllLogs);

    const logo = document.querySelector(".logo");
    logo.addEventListener("click", () => {
        window.location.href = "../index.html";
    });

    cargarLogs();
});

function mostrarDetalles(texto) {
    document.getElementById('descText').textContent = texto;
    document.getElementById('descModal').style.display = 'flex';
}

function cerrarModal() {
    document.getElementById('descModal').style.display = 'none';
}

window.onclick = function(event) {
    const modal = document.getElementById('descModal');
    if (event.target === modal) cerrarModal();
};
