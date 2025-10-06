document.addEventListener("DOMContentLoaded", () => {
    const usernameBtn = document.getElementById("usernameBtn");
    const dropdown = document.getElementById("userDropdown");
    const logoutBtn = document.getElementById("logoutBtn");
    const navMenu = document.getElementById("navMenu");

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
        usernameBtn.textContent = username;
        logoutBtn.style.display = "block";

        navMenu.innerHTML = `
          <a href="vehiculos.html">Vehículos</a>
          <a href="repuestos.html">Repuestos</a>
          <a href="presupuestos.html">Presupuestos</a>
          <a href="usuarios.html">Usuarios</a>
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
        window.location.reload();
    });

    document.addEventListener("click", (e) => {
        if (!usernameBtn.contains(e.target) && !dropdown.contains(e.target)) {
            dropdown.style.display = "none";
        }
    });

    const urlParams = new URLSearchParams(window.location.search);
    const repuestoId = urlParams.get("id");
    const formTitle = document.getElementById("formTitle");
    const repuestoForm = document.getElementById("repuestoForm");

    if (repuestoId) {
        formTitle.textContent = "Editar Repuesto";
        fetch(`/repuestos/${repuestoId}`, {
            headers: { "Authorization": `Bearer ${token}` }
        })
            .then(res => res.json())
            .then(data => {
                document.getElementById("repuestoId").value = data.id;
                document.getElementById("nombre").value = data.nombre;
                document.getElementById("descripcion").value = data.descripcion;
                document.getElementById("stock").value = data.stock;
                document.getElementById("precio").value = data.precio;
            })
            .catch(err => alert("Error al cargar el repuesto"));
    }

    repuestoForm.addEventListener("submit", (e) => {
        e.preventDefault();

        const repuestoData = {
            nombre: document.getElementById("nombre").value,
            descripcion: document.getElementById("descripcion").value,
            stock: parseInt(document.getElementById("stock").value),
            precio: parseFloat(document.getElementById("precio").value)
        };

        let method = "POST";
        let url = "/repuestos";

        if (repuestoId) {
            method = "PUT";
            url = `/repuestos/${repuestoId}`;
        }

        fetch(url, {
            method: method,
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(repuestoData)
        })
            .then(res => {
                if (!res.ok) throw new Error("Error al guardar el repuesto");
                window.location.href = "repuestos.html";
            })
            .catch(err => alert(err));
    });

    document.getElementById("cancelBtn").addEventListener("click", () => {
        window.location.href = "repuestos.html";
    });
});

document.addEventListener("DOMContentLoaded", () => {
    const logo = document.querySelector(".logo");
    logo.addEventListener("click", () => {
        window.location.href = "../index.html";
    });
});

