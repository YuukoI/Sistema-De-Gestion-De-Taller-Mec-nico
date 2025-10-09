document.addEventListener("DOMContentLoaded", () => {
    const usernameBtn = document.getElementById("usernameBtn");
    const dropdown = document.getElementById("userDropdown");
    const logoutBtn = document.getElementById("logoutBtn");
    const navMenu = document.getElementById("navMenu");
    const formTitle = document.getElementById("formTitle");
    const vehiculoForm = document.getElementById("vehiculoForm");
    const cancelBtn = document.getElementById("cancelBtn");
    const logo = document.querySelector(".logo");

    const token = localStorage.getItem("jwt");

    if (!token) {
        alert("Debes iniciar sesión");
        window.location.href = "../index.html";
        return;
    }

    function parseJwt(token) {
        try {
            const payload = token.split(".")[1];
            return JSON.parse(atob(payload));
        } catch (e) {
            return null;
        }
    }

    const decoded = parseJwt(token);
    const username = decoded?.sub || decoded?.username || "Usuario";
    const role = decoded?.role || decoded?.rol || decoded?.roles?.[0] || "USER";

    usernameBtn.textContent = username;
    logoutBtn.style.display = "block";

    let navHtml = `
        <a href="vehiculos.html">Vehículos</a>
        <a href="repuestos.html">Repuestos</a>
        <a href="presupuestos.html">Presupuestos</a>
    `;
    if (role === "ADMIN") {
        navHtml += `<a href="usuarios.html">Usuarios</a>`;
    }
    navMenu.innerHTML = navHtml;

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
        window.location.href = "formLogin.html";
    });

    logo.addEventListener("click", () => {
        window.location.href = "../index.html";
    });

    const urlParams = new URLSearchParams(window.location.search);
    const vehiculoId = urlParams.get("id");

    if (vehiculoId) {
        formTitle.textContent = "Editar Vehículo";
        fetch(`/vehiculos/${vehiculoId}`, {
            headers: { "Authorization": `Bearer ${token}` }
        })
            .then(res => {
                if (!res.ok) throw new Error("Error al cargar el vehículo");
                return res.json();
            })
            .then(data => {
                document.getElementById("vehiculoId").value = data.id;
                document.getElementById("patente").value = data.patente;
                document.getElementById("marca").value = data.marca;
                document.getElementById("modelo").value = data.modelo;
                document.getElementById("dueno").value = data.nombrePropietario || "";
            })
            .catch(() => alert("Error al cargar el vehículo"));
    }

    vehiculoForm.addEventListener("submit", (e) => {
        e.preventDefault();

        const vehiculoData = {
            patente: document.getElementById("patente").value.trim(),
            marca: document.getElementById("marca").value.trim(),
            modelo: document.getElementById("modelo").value.trim(),
            nombrePropietario: document.getElementById("dueno").value.trim()
        };

        const method = vehiculoId ? "PUT" : "POST";
        const url = vehiculoId ? `/vehiculos/${vehiculoId}` : "/vehiculos";

        fetch(url, {
            method,
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(vehiculoData)
        })
            .then(res => {
                if (!res.ok) throw new Error("Error al guardar el vehículo");
                return res.json();
            })
            .then(() => {
                window.location.href = "vehiculos.html";
            })
            .catch(err => alert(err.message));
    });

    cancelBtn.addEventListener("click", () => {
        window.location.href = "vehiculos.html";
    });
});
