document.addEventListener("DOMContentLoaded", () => {
    const usernameBtn = document.getElementById("usernameBtn");
    const dropdown = document.getElementById("userDropdown");
    const logoutBtn = document.getElementById("logoutBtn");
    const navMenu = document.getElementById("navMenu");

    let paginaActual = 0;
    const tamañoPagina = 15;
    let totalPaginas = 0;
    let userRole = null;

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

    if (token) {
        const decoded = parseJwt(token);
        username = decoded?.sub || decoded?.username || "Usuario";
        userRole = decoded?.sub || "USER"; // sub será "ADMIN" o "USER"
        usernameBtn.textContent = username;
        logoutBtn.style.display = "block";

        navMenu.innerHTML = `
          <a href="vehiculos.html">Vehículos</a>
          <a href="repuestos.html">Repuestos</a>
          <a href="presupuestos.html">Presupuestos</a>
          <a href="usuarios.html">Usuarios</a>
        `;
    } else {
        usernameBtn.textContent = "Acceder";
        logoutBtn.style.display = "none";
        navMenu.innerHTML = "";
        alert("Debes iniciar sesión");
        window.location.href = "../index.html";
        return;
    }

    usernameBtn.addEventListener("click", () => {
        if (username) {
            dropdown.style.display = dropdown.style.display === "flex" ? "none" : "flex";
        } else {
            window.location.href = "html/formLogin.html";
        }
    });

    logoutBtn.addEventListener("click", () => {
        localStorage.removeItem("jwt");
        localStorage.removeItem("username");
        window.location.reload();
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
                        }
                        tbody.append(`
                            <tr>
                                <td>${v.id}</td>
                                <td>${v.patente}</td>
                                <td>${v.marca || ""}</td>
                                <td>${v.modelo || ""}</td>
                                <td>${v.nombrePropietario || ""}</td>
                                <td>${acciones}</td>
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
                alert("Error al cargar vehículos");
                console.error(err);
            }
        });
    };

    $("#searchFiltro").on("input", function() {
        paginaActual = 0;
        cargarVehiculos($(this).val());
    });

    if (userRole === "ADMIN") {
        $("#agregarVehiculoBtn").click(() => {
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
});

document.addEventListener("DOMContentLoaded", () => {
    const logo = document.querySelector(".logo");
    logo.addEventListener("click", () => {
        window.location.href = "../index.html";
    });
});
