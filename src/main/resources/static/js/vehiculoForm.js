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
    const vehiculoId = urlParams.get("id");
    const formTitle = document.getElementById("formTitle");
    const vehiculoForm = document.getElementById("vehiculoForm");

    if (vehiculoId) {
        formTitle.textContent = "Editar Vehículo";
        fetch(`/vehiculos/${vehiculoId}`, {
            headers: { "Authorization": `Bearer ${token}` }
        })
            .then(res => res.json())
            .then(data => {
                document.getElementById("vehiculoId").value = data.id;
                document.getElementById("patente").value = data.patente;
                document.getElementById("marca").value = data.marca;
                document.getElementById("modelo").value = data.modelo;
                document.getElementById("dueno").value = data.nombrePropietario || "";
            })
            .catch(err => alert("Error al cargar el vehículo"));
    }

    vehiculoForm.addEventListener("submit", (e) => {
        e.preventDefault();

        const vehiculoData = {
            patente: document.getElementById("patente").value,
            marca: document.getElementById("marca").value,
            modelo: document.getElementById("modelo").value,
            nombrePropietario: document.getElementById("dueno").value
        };

        let method = "POST";
        let url = "/vehiculos";

        if (vehiculoId) {
            method = "PUT";
            url = `/vehiculos/${vehiculoId}`;
        }

        fetch(url, {
            method: method,
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(vehiculoData)
        })
            .then(res => {
                if (!res.ok) throw new Error("Error al guardar el vehículo");
                window.location.href = "vehiculos.html";
            })
            .catch(err => alert(err));
    });

    document.getElementById("cancelBtn").addEventListener("click", () => {
        window.location.href = "vehiculos.html";
    });
});

document.addEventListener("DOMContentLoaded", () => {
    const logo = document.querySelector(".logo");
    logo.addEventListener("click", () => {
        window.location.href = "../index.html";
    });
});

