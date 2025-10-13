function cerrarModal() {
    document.getElementById('modeloModal').style.display = 'none';
}

window.onclick = function(event) {
    const modal = document.getElementById('modeloModal');
    if (event.target === modal) {
        cerrarModal();
    }
};

document.addEventListener("DOMContentLoaded", () => {
    const usernameBtn = document.getElementById("usernameBtn");
    const dropdown = document.getElementById("userDropdown");
    const logoutBtn = document.getElementById("logoutBtn");
    const navMenu = document.getElementById("navMenu");

    let paginaActual = 0;
    const tamañoPagina = 15;
    let totalPaginas = 0;
    let userRole = "USER";

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
    const username = decoded?.sub || "Usuario";
    userRole = decoded?.rol || "USER";

    usernameBtn.textContent = username;
    logoutBtn.style.display = "block";

    let navHtml = `
        <a href="vehiculos.html">Vehículos</a>
        <a href="repuestos.html">Repuestos</a>
        <a href="presupuestos.html">Presupuestos</a>
    `;
    if (userRole === "ADMIN") {
        navHtml += `<a href="usuarios.html">Usuarios</a>`;
        navHtml += `<a href="logs.html">Auditoría</a>`;
    }
    navMenu.innerHTML = navHtml;

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

    const cargarVehiculos = (filtro = "") => {
        let url = `/vehiculos?page=${paginaActual}&size=${tamañoPagina}`;
        if (filtro) {
            url = `/vehiculos/search?keyword=${encodeURIComponent(filtro)}&page=${paginaActual}&size=${tamañoPagina}`;
        }

        $.ajax({
            url: url,
            method: "GET",
            headers: { "Authorization": `Bearer ${token}` },
            success: function(data) {
                const vehiculos = data.content || [];
                const tbody = $("#vehiculosTableBody");
                tbody.empty();

                if (vehiculos.length === 0) {
                    tbody.append(`<tr><td colspan="6" class="text-center">No se encontraron vehículos</td></tr>`);
                } else {
                    vehiculos.forEach(v => {
                        let acciones = "";
                        if (userRole === "ADMIN") {
                            acciones = `
                                <button class="btn btn-sm btn-warning" onclick="editarVehiculo(${v.id})">Editar</button>
                                <button class="btn btn-sm btn-danger" onclick="borrarVehiculo(${v.id})">Borrar</button>
                            `;
                        } else {
                            acciones = "-";
                        }


                        tbody.append(`
                            <tr>
                                <td class="id" title="${v.id}">${v.id}</td>
                                <td class="patente" title="${v.patente}">${v.patente}</td>
                                <td class="marca" title="${v.marca || ''}">${v.marca || ''}</td>
                                <td class="modelo modeloCell" title="${v.modelo || ''}">
                                    ${v.modelo && v.modelo.length > 40 ? v.modelo.substring(0, 40) + "..." : v.modelo || ''}
                                </td>
                                <td class="nombrePropietario" title="${v.nombrePropietario || ''}">${v.nombrePropietario || ''}</td>
                                <td class="acciones">${acciones}</td>
                            </tr>
                        `);
                    });
                }

                totalPaginas = data.totalPages || 1;
                $("#paginaActual").text(`Página ${data.number + 1} de ${totalPaginas}`);
                $("#prevBtn").prop("disabled", data.first);
                $("#nextBtn").prop("disabled", data.last);

                const modal = document.getElementById("modeloModal");
                const modeloTexto = document.getElementById("modeloTexto");
                const closeBtn = modal.querySelector(".close-btn");

                $(".modeloCell").css("cursor", "pointer").on("click", function() {
                    modeloTexto.textContent = $(this).attr("title");
                    modal.style.display = "flex";
                });

                closeBtn.onclick = () => cerrarModal();
            },
            error: function(err) {
                if (err.status === 403) {
                    alert("No autorizado. Por favor, inicia sesión nuevamente.");
                    localStorage.removeItem("jwt");
                    window.location.href = "../index.html";
                } else {
                    alert("Error al cargar vehículos");
                    console.error(err);
                }
            }
        });
    };

    $("#searchFiltro").on("input", function() {
        paginaActual = 0;
        cargarVehiculos($(this).val());
    });

    if (userRole === "ADMIN") {
        $("#agregarVehiculoBtn").show().click(() => {
            window.location.href = "vehiculoForm.html";
        });
    } else {
        $("#agregarVehiculoBtn").hide();
    }

    window.borrarVehiculo = (id) => {
        if (userRole !== "ADMIN") return;
        if (confirm("¿Desea borrar este vehículo?")) {
            $.ajax({
                url: `/vehiculos/${id}`,
                method: "DELETE",
                headers: { "Authorization": `Bearer ${token}` },
                success: () => cargarVehiculos($("#searchFiltro").val()),
                error: () => alert("Error al borrar el vehículo")
            });
        }
    };

    window.editarVehiculo = (id) => {
        if (userRole !== "ADMIN") return;
        window.location.href = `vehiculoForm.html?id=${id}`;
    };

    $("#prevBtn").click(() => {
        if (paginaActual > 0) {
            paginaActual--;
            cargarVehiculos($("#searchFiltro").val());
        }
    });

    $("#nextBtn").click(() => {
        if (paginaActual + 1 < totalPaginas) {
            paginaActual++;
            cargarVehiculos($("#searchFiltro").val());
        }
    });

    cargarVehiculos();

    const logo = document.querySelector(".logo");
    logo.addEventListener("click", () => {
        window.location.href = "../index.html";
    });
});
