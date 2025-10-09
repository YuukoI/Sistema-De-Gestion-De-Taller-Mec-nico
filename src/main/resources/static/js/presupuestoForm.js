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
    let esAdmin = false;

    if (token) {
        const decoded = parseJwt(token);
        username = decoded?.sub || decoded?.username || "Usuario";
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
    const presupuestoId = urlParams.get("id");
    const formTitle = document.getElementById("formTitle");
    const presupuestoForm = document.getElementById("presupuestoForm");

    if (presupuestoId) {
        formTitle.textContent = "Editar Presupuesto";
        fetch(`/presupuestos/${presupuestoId}`, {
            headers: { "Authorization": `Bearer ${token}` }
        })
            .then(res => res.json())
            .then(data => {
                document.getElementById("presupuestoId").value = data.id;
                document.getElementById("patente").value = data.patente || "";
                document.getElementById("nombrePropietario").value = data.nombrePropietario || "";
                document.getElementById("marca").value = data.marca || "";
                document.getElementById("modelo").value = data.modelo || "";
                document.getElementById("manoDeObra").value = data.manoDeObra || "";
                document.getElementById("descripcion").value = data.descripcion || "";
                document.getElementById("total").value = data.total || "";
            })
            .catch(() => alert("Error al cargar el presupuesto"));
    }

    presupuestoForm.addEventListener("submit", (e) => {
        e.preventDefault();

        const presupuestoData = {
            patente: document.getElementById("patente").value,
            nombrePropietario: document.getElementById("nombrePropietario").value,
            marca: document.getElementById("marca").value,
            modelo: document.getElementById("modelo").value,
            manoDeObra: parseFloat(document.getElementById("manoDeObra").value) || 0,
            descripcion: document.getElementById("descripcion").value,
            total: parseFloat(document.getElementById("total").value) || 0
        };

        let method = "POST";
        let url = "/presupuestos";

        if (presupuestoId) {
            method = "PUT";
            url = `/presupuestos/${presupuestoId}`;
        }

        fetch(url, {
            method,
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(presupuestoData)
        })
            .then(res => {
                if (!res.ok) throw new Error("Error al guardar el presupuesto");
                window.location.href = "presupuestos.html";
            })
            .catch(err => alert(err.message));
    });

    document.getElementById("cancelBtn").addEventListener("click", () => {
        window.location.href = "presupuestos.html";
    });

    const logo = document.querySelector(".logo");
    logo.addEventListener("click", () => {
        window.location.href = "../index.html";
    });
});
