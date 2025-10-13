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
        usernameBtn.textContent = username;
        logoutBtn.style.display = "block";

        esAdmin = decoded?.rol === "ADMIN";

        let navHtml = `
            <a href="vehiculos.html">Vehículos</a>
            <a href="repuestos.html">Repuestos</a>
            <a href="presupuestos.html">Presupuestos</a>
        `;
        if (esAdmin) {
            navHtml += `<a href="usuarios.html">Usuarios</a>`;
            navHtml += `<a href="logs.html">Auditoría</a>`;
        }
        navMenu.innerHTML = navHtml;

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
        window.location.href = "formLogin.html";
    });

    document.addEventListener("click", (e) => {
        if (!usernameBtn.contains(e.target) && !dropdown.contains(e.target)) {
            dropdown.style.display = "none";
        }
    });

    const actualizarStock = (id, nuevoStock) => {
        $.ajax({
            url: `/repuestos/${id}/stock`,
            method: "PATCH",
            headers: { "Authorization": `Bearer ${token}`, "Content-Type": "application/json" },
            data: JSON.stringify({ stock: nuevoStock }),
            success: () => cargarRepuestos($("#searchNombre").val()),
            error: () => alert("Error al actualizar el stock")
        });
    };

    const cargarRepuestos = (nombre = "") => {
        let url = `/repuestos?page=${paginaActual}&size=${tamañoPagina}`;
        if (nombre) {
            url = `/repuestos/search?nombre=${nombre}&page=${paginaActual}&size=${tamañoPagina}`;
        }

        $.ajax({
            url: url,
            method: "GET",
            headers: { "Authorization": `Bearer ${token}` },
            success: function(data) {
                const repuestos = data.content || data;
                const tbody = $("#repuestosTableBody");
                tbody.empty();

                repuestos.forEach(r => {
                    const descripcionCorta = r.descripcion ? (r.descripcion.length > 100 ? r.descripcion.substring(0, 100) + '...' : r.descripcion) : "";
                    const descripcionCompleta = r.descripcion || "";
                    tbody.append(`
<tr>
    <td>${r.id}</td>
    <td class="nombre" title="${r.nombre}">${r.nombre}</td>
    <td class="descripcion" onclick="mostrarDescripcion('${descripcionCompleta.replace(/'/g, "\\'")}')" title="Haz clic para ver completa">
        ${descripcionCorta}
    </td>
    <td>
        ${esAdmin ? `
            <button class="btn btn-sm btn-outline-danger" onclick="cambiarStock(${r.id}, ${r.stock - 1})">-</button>
            <span style="margin: 0 8px; font-weight: bold;">${r.stock}</span>
            <button class="btn btn-sm btn-outline-success" onclick="cambiarStock(${r.id}, ${r.stock + 1})">+</button>
        ` : r.stock}
    </td>
    <td>${r.precio.toLocaleString('es-AR', { style: 'currency', currency: 'ARS' })}</td>
    <td class="acciones">
        ${esAdmin ? `
            <button class="btn btn-sm btn-warning" onclick="editarRepuesto(${r.id})">Editar</button>
            <button class="btn btn-sm btn-danger" onclick="borrarRepuesto(${r.id})">Borrar</button>
        ` : '-'}
       
    </td>
</tr>
                    `);
                });

                totalPaginas = data.totalPages || 1;
                $("#paginaActual").text(`Página ${data.number + 1} de ${totalPaginas}`);
                $("#prevBtn").prop("disabled", data.first);
                $("#nextBtn").prop("disabled", data.last);
            },
            error: function(err) {
                alert("Error al cargar repuestos");
                console.error(err);
            }
        });
    };

    window.cambiarStock = (id, nuevoStock) => {
        if (nuevoStock < 0) {
            alert("El stock no puede ser negativo");
            return;
        }
        actualizarStock(id, nuevoStock);
    };

    $("#searchNombre").on("input", function() {
        paginaActual = 0;
        cargarRepuestos($(this).val());
    });

    $("#agregarRepuestoBtn").toggle(esAdmin);
    $("#agregarRepuestoBtn").click(() => {
        if (esAdmin) {
            window.location.href = "repuestoForm.html";
        }
    });

    window.borrarRepuesto = (id) => {
        if (!esAdmin) return;
        if(confirm("¿Desea borrar este repuesto?")) {
            $.ajax({
                url: `/repuestos/${id}`,
                method: "DELETE",
                headers: { "Authorization": `Bearer ${token}` },
                success: () => cargarRepuestos($("#searchNombre").val()),
                error: () => alert("Error al borrar el repuesto")
            });
        }
    };

    window.editarRepuesto = (id) => {
        if (!esAdmin) return;
        window.location.href = `repuestoForm.html?id=${id}`;
    };

    $("#prevBtn").click(() => {
        if (paginaActual > 0) {
            paginaActual--;
            cargarRepuestos($("#searchNombre").val());
        }
    });

    $("#nextBtn").click(() => {
        if (paginaActual + 1 < totalPaginas) {
            paginaActual++;
            cargarRepuestos($("#searchNombre").val());
        }
    });

    cargarRepuestos();

    const logo = document.querySelector(".logo");
    logo.addEventListener("click", () => {
        window.location.href = "../index.html";
    });
});

function mostrarDescripcion(texto) {
    document.getElementById('descText').textContent = texto;
    document.getElementById('descModal').style.display = 'flex';
}

function cerrarModal() {
    document.getElementById('descModal').style.display = 'none';
}

window.onclick = function(event) {
    const modal = document.getElementById('descModal');
    if (event.target === modal) {
        cerrarModal();
    }
};
