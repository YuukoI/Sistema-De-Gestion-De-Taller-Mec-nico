document.addEventListener("DOMContentLoaded", () => {
    const usernameBtn = document.getElementById("usernameBtn");
    const dropdown = document.getElementById("userDropdown");
    const logoutBtn = document.getElementById("logoutBtn");
    const navMenu = document.getElementById("navMenu");
    const formTitle = document.getElementById("formTitle");
    const repuestoForm = document.getElementById("repuestoForm");
    const cancelBtn = document.getElementById("cancelBtn");

    const token = localStorage.getItem("jwt");
    if (!token) {
        alert("Debes iniciar sesión");
        window.location.href = "../index.html";
        return;
    }

    function parseJwt(token) {
        try {
            return JSON.parse(atob(token.split(".")[1]));
        } catch {
            return null;
        }
    }

    const decoded = parseJwt(token);
    const username = decoded?.sub || decoded?.username || "Usuario";
    const esAdmin = decoded?.rol === "ADMIN";

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

    usernameBtn.addEventListener("click", () => {
        dropdown.style.display = dropdown.style.display === "flex" ? "none" : "flex";
    });

    document.addEventListener("click", (e) => {
        if (!usernameBtn.contains(e.target) && !dropdown.contains(e.target)) {
            dropdown.style.display = "none";
        }
    });

    logoutBtn.addEventListener("click", () => {
        localStorage.removeItem("jwt");
        window.location.href = "../index.html";
    });

    document.querySelector(".logo")?.addEventListener("click", () => {
        window.location.href = "../index.html";
    });

    const urlParams = new URLSearchParams(window.location.search);
    const repuestoId = urlParams.get("id");

    if (repuestoId) {
        formTitle.textContent = "Editar Repuesto";
        fetch(`/repuestos/${repuestoId}`, {
            headers: { "Authorization": `Bearer ${token}` }
        })
            .then(res => {
                if (!res.ok) throw new Error("Error al cargar el repuesto");
                return res.json();
            })
            .then(data => {
                document.getElementById("repuestoId").value = data.id;
                document.getElementById("nombre").value = data.nombre;
                document.getElementById("descripcion").value = data.descripcion;
                document.getElementById("stock").value = data.stock;
                document.getElementById("precio").value = data.precio;
            })
            .catch(() => alert("Error al cargar el repuesto"));
    }

    repuestoForm.addEventListener("submit", (e) => {
        e.preventDefault();

        const repuestoData = {
            nombre: document.getElementById("nombre").value.trim(),
            descripcion: document.getElementById("descripcion").value.trim(),
            stock: parseInt(document.getElementById("stock").value) || 0,
            precio: parseFloat(document.getElementById("precio").value) || 0
        };

        const method = repuestoId ? "PUT" : "POST";
        const url = repuestoId ? `/repuestos/${repuestoId}` : "/repuestos";

        fetch(url, {
            method,
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(repuestoData)
        })
            .then(res => {
                if (!res.ok) throw new Error("Error al guardar el repuesto");
                return res.json();
            })
            .then(() => window.location.href = "repuestos.html")
            .catch(err => alert(err.message));
    });

    cancelBtn.addEventListener("click", () => {
        window.location.href = "repuestos.html";
    });
});
