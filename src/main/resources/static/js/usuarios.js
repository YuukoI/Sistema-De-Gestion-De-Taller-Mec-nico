document.addEventListener("DOMContentLoaded", () => {
    const usernameBtn = document.getElementById("usernameBtn");
    const dropdown = document.getElementById("userDropdown");
    const logoutBtn = document.getElementById("logoutBtn");
    const navMenu = document.getElementById("navMenu");

    let paginaActual = 0;
    const tamañoPagina = 15;
    let totalPaginas = 0;
    let esAdmin = false;

    const token = localStorage.getItem("jwt");

    function parseJwt(token) {
        try {
            return JSON.parse(atob(token.split('.')[1]));
        } catch {
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
    esAdmin = decoded?.rol === "ADMIN";
    usernameBtn.textContent = username;
    logoutBtn.style.display = "block";

    let navHtml = `
        <a href="vehiculos.html">Vehículos</a>
        <a href="repuestos.html">Repuestos</a>
        <a href="presupuestos.html">Presupuestos</a>
    `;
    if (esAdmin) {
        navHtml += `<a href="usuarios.html">Usuarios</a>`;
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

    const cargarUsuarios = (filtro = "") => {
        let url = `/usuarios?page=${paginaActual}&size=${tamañoPagina}`;
        if (filtro) {
            url = `/usuarios/search?keyword=${encodeURIComponent(filtro)}&page=${paginaActual}&size=${tamañoPagina}`;
        }

        $.ajax({
            url,
            method: "GET",
            headers: { "Authorization": `Bearer ${token}` },
            success: function(data) {
                const usuarios = data.content || [];
                const tbody = $("#usuariosTableBody");
                tbody.empty();

                if (usuarios.length === 0) {
                    tbody.append(`<tr><td colspan="7" class="text-center">No se encontraron usuarios</td></tr>`);
                } else {
                    usuarios.forEach(u => {
                        let acciones = "";
                        if (esAdmin) {
                            acciones = `
                                <button class="btn btn-sm btn-warning" onclick="editarUsuario(${u.id})">Editar</button>
                                <button class="btn btn-sm btn-danger" onclick="borrarUsuario(${u.id})">Borrar</button>
                            `;
                        }

                        tbody.append(`
                            <tr>
                                <td>${u.id}</td>
                                <td>${u.username}</td>
                                <td>${u.firstName || ""}</td>
                                <td>${u.lastName || ""}</td>
                                <td>${u.country || ""}</td>
                                <td>${u.role}</td>
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
            error: () => alert("Error al cargar usuarios")
        });
    };

    $("#searchFiltro").on("input", function() {
        paginaActual = 0;
        cargarUsuarios($(this).val());
    });

    window.borrarUsuario = (id) => {
        if (!esAdmin) return;
        if(confirm("¿Desea borrar este usuario?")) {
            $.ajax({
                url: `/usuarios/${id}`,
                method: "DELETE",
                headers: { "Authorization": `Bearer ${token}` },
                success: () => cargarUsuarios($("#searchFiltro").val()),
                error: () => alert("Error al borrar el usuario")
            });
        }
    };

    window.editarUsuario = (id) => {
        if (!esAdmin) return;
        window.location.href = `usuariosForm.html?id=${id}`;
    };

    $("#prevBtn").click(() => {
        if (paginaActual > 0) {
            paginaActual--;
            cargarUsuarios($("#searchFiltro").val());
        }
    });

    $("#nextBtn").click(() => {
        if (paginaActual + 1 < totalPaginas) {
            paginaActual++;
            cargarUsuarios($("#searchFiltro").val());
        }
    });

    cargarUsuarios();

    const logo = document.querySelector(".logo");
    logo.addEventListener("click", () => window.location.href = "../index.html");
});
