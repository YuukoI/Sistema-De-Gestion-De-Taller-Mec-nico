document.addEventListener("DOMContentLoaded", () => {
    const usernameBtn = document.getElementById("usernameBtn");
    const dropdown = document.getElementById("userDropdown");
    const logoutBtn = document.getElementById("logoutBtn");
    const navMenu = document.getElementById("navMenu");
    const formTitle = document.getElementById("formTitle");
    const usuarioForm = document.getElementById("usuarioForm");
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

    logoutBtn.addEventListener("click", () => {
        localStorage.removeItem("jwt");
        window.location.href = "../index.html";
    });

    document.addEventListener("click", (e) => {
        if (!usernameBtn.contains(e.target) && !dropdown.contains(e.target)) {
            dropdown.style.display = "none";
        }
    });

    document.querySelector(".logo")?.addEventListener("click", () => {
        window.location.href = "../index.html";
    });

    const urlParams = new URLSearchParams(window.location.search);
    const usuarioId = urlParams.get("id");
    if (!usuarioId) {
        alert("No se especificó el usuario a editar");
        window.location.href = "usuarios.html";
        return;
    }

    fetch(`/usuarios/${usuarioId}`, {
        headers: { "Authorization": `Bearer ${token}` }
    })
        .then(res => {
            if (!res.ok) throw new Error("Error al cargar usuario");
            return res.json();
        })
        .then(data => {
            formTitle.textContent = "Editar Usuario";
            document.getElementById("usuarioId").value = data.id;
            document.getElementById("username").value = data.username;
            document.getElementById("firstName").value = data.firstName || "";
            document.getElementById("lastName").value = data.lastName || "";
            document.getElementById("country").value = data.country || "";
            document.getElementById("role").value = data.role;
        })
        .catch(err => alert(err.message));

    usuarioForm.addEventListener("submit", (e) => {
        e.preventDefault();

        const updatedUser = {
            username: document.getElementById("username").value.trim(),
            firstName: document.getElementById("firstName").value.trim(),
            lastName: document.getElementById("lastName").value.trim(),
            country: document.getElementById("country").value.trim(),
            role: document.getElementById("role").value
        };

        const pwInput = document.getElementById("password");
        if (pwInput && pwInput.value.trim()) {
            updatedUser.password = pwInput.value.trim();
        }

        fetch(`/usuarios/${usuarioId}`, {
            method: "PUT",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(updatedUser)
        })
            .then(res => {
                if (!res.ok) throw new Error("Error al actualizar usuario");
                window.location.href = "usuarios.html";
            })
            .catch(err => alert(err.message));
    });

    cancelBtn.addEventListener("click", () => {
        window.location.href = "usuarios.html";
    });
});
