document.addEventListener("DOMContentLoaded", () => {
    const usernameBtn = document.getElementById("usernameBtn");
    const dropdown = document.getElementById("userDropdown");
    const logoutBtn = document.getElementById("logoutBtn");
    const navMenu = document.getElementById("navMenu");

    let paginaActual = 0;
    const tamañoPagina = 15;
    let totalPaginas = 0;
    let userRole = null;

    const token = localStorage.getItem("jwt");

    function parseJwt(token) {
        try {
            return JSON.parse(atob(token.split('.')[1]));
        } catch (e) {
            return null;
        }
    }

    if (!token) {
        alert("Debes iniciar sesión");
        window.location.href = "../index.html";
        return;
    }

    const decoded = parseJwt(token);
    const username = decoded?.sub || decoded?.username || "Usuario";
    userRole = decoded?.sub || "USER"; // sub será "ADMIN" o "USER"
    usernameBtn.textContent = username;
    logoutBtn.style.display = "block";

    // Navbar según rol
    let navHtml = `
        <a href="vehiculos.html">Vehículos</a>
        <a href="repuestos.html">Repuestos</a>
        <a href="presupuestos.html">Presupuestos</a>
    `;
    if (userRole === "ADMIN") {
        navHtml += `<a href="usuarios.html">Usuarios</a>`;
    }
    navMenu.innerHTML = navHtml;

    usernameBtn.addEventListener("click", () => {
        dropdown.style.display = dropdown.style.display === "flex" ? "none" : "flex";
    });

    logoutBtn.addEventListener("click", () => {
        localStorage.removeItem("jwt");
        window.location.reload();
    });

    document.addEventListener("click", (e) => {
        if (!usernameBtn.contains(e.target) && !dropdown.contains(e.target)) {
            dropdown.style.display = "none";
        }
    });

    // Mostrar o ocultar botón "Agregar" según rol
    if (userRole !== "ADMIN") {
        document.getElementById("agregarPresupuestoBtn").style.display = "none";
    }

    const cargarPresupuestos = (filtro = "") => {
        let url = `/presupuestos?page=${paginaActual}&size=${tamañoPagina}`;
        if (filtro) {
            url = `/presupuestos/search?keyword=${encodeURIComponent(filtro)}&page=${paginaActual}&size=${tamañoPagina}`;
        }

        $.ajax({
            url: url,
            method: "GET",
            headers: { "Authorization": `Bearer ${token}` },
            success: function(data) {
                const presupuestos = data.content || [];
                const tbody = $("#presupuestosTableBody");
                tbody.empty();

                if (presupuestos.length === 0) {
                    tbody.append(`<tr><td colspan="8" class="text-center">No se encontraron presupuestos</td></tr>`);
                } else {
                    presupuestos.forEach(p => {
                        const fecha = p.fecha ? new Date(p.fecha + "T00:00:00").toLocaleDateString() : "";
                        const descripcion = p.descripcion || (p.repuestos ? p.repuestos.join(", ") + (p.manoDeObra ? " + Mano de obra" : "") : "");

                        let acciones = "";
                        if (userRole === "ADMIN") {
                            acciones = `
                                <button class="btn btn-sm btn-warning" onclick="editarPresupuesto(${p.id})">Editar</button>
                                <button class="btn btn-sm btn-danger" onclick="borrarPresupuesto(${p.id})">Borrar</button>
                            `;
                        }

                        tbody.append(`
                            <tr>
                                <td>${p.id}</td>
                                <td>${p.patente}</td>
                                <td>${p.nombrePropietario}</td>
                                <td>${descripcion}</td>
                                <td>${p.total || ""}</td>
                                <td>${fecha}</td>
                                <td>${acciones}</td>
                                <td>
                                    <button class="btn btn-sm btn-secondary" onclick="verPdf(${p.id})">
                                        <i class="bi bi-file-earmark-pdf"></i>
                                    </button>
                                </td>
                            </tr>
                        `);
                    });
                }

                totalPaginas = data.totalPages || 1;
                $("#paginaActual").text(`Página ${data.number + 1} de ${totalPaginas}`);
                $("#prevBtn").prop("disabled", data.first);
                $("#nextBtn").prop("disabled", data.last);
            },
            error: function(err) {
                if (err.status === 403) {
                    alert("No autorizado. Por favor, inicia sesión nuevamente.");
                    localStorage.removeItem("jwt");
                    window.location.href = "../index.html";
                } else {
                    alert("Error al cargar presupuestos");
                    console.error(err);
                }
            }
        });
    };

    $("#searchFiltro").on("input", function() {
        paginaActual = 0;
        cargarPresupuestos($(this).val());
    });

    $("#agregarPresupuestoBtn").click(() => {
        if (userRole === "ADMIN") {
            window.location.href = "presupuestoForm.html";
        }
    });

    window.borrarPresupuesto = (id) => {
        if (userRole !== "ADMIN") return;
        if (confirm("¿Desea borrar este presupuesto?")) {
            $.ajax({
                url: `/presupuestos/${id}`,
                method: "DELETE",
                headers: { "Authorization": `Bearer ${token}` },
                success: () => cargarPresupuestos($("#searchFiltro").val()),
                error: () => alert("Error al borrar el presupuesto")
            });
        }
    };

    window.editarPresupuesto = (id) => {
        if (userRole !== "ADMIN") return;
        window.location.href = `presupuestoForm.html?id=${id}`;
    };

    $("#prevBtn").click(() => {
        if (paginaActual > 0) {
            paginaActual--;
            cargarPresupuestos($("#searchFiltro").val());
        }
    });

    $("#nextBtn").click(() => {
        if (paginaActual + 1 < totalPaginas) {
            paginaActual++;
            cargarPresupuestos($("#searchFiltro").val());
        }
    });

    cargarPresupuestos();

    const logo = document.querySelector(".logo");
    logo.addEventListener("click", () => {
        window.location.href = "../index.html";
    });
});

window.verPdf = async (id) => {
    const token = localStorage.getItem("jwt");

    try {
        const response = await fetch(`/presupuestos/${id}/pdf`, {
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (!response.ok) {
            alert("No se pudo generar el PDF");
            return;
        }

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);

        const contenedor = document.createElement("div");
        contenedor.innerHTML = `
            <div style="position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.7); display:flex; align-items:center; justify-content:center; z-index:9999;">
                <div style="background:#fff; padding:20px; width:80%; height:90%; position:relative;">
                    <button id="cerrarPdf" style="position:absolute; top:10px; right:10px;" class="btn btn-danger">Cerrar</button>
                    <iframe src="${url}" style="width:100%; height:85%; border:none;"></iframe>
                    <div style="text-align:center; margin-top:10px;">
                        <a href="${url}" download="presupuesto_${id}.pdf" class="btn btn-success">Descargar PDF</a>
                    </div>
                </div>
            </div>
        `;

        document.body.appendChild(contenedor);

        document.getElementById("cerrarPdf").addEventListener("click", () => {
            contenedor.remove();
            window.URL.revokeObjectURL(url);
        });

    } catch (error) {
        console.error("Error al generar el PDF:", error);
        alert("Ocurrió un error al mostrar el PDF");
    }
};
