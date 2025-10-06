document.addEventListener("DOMContentLoaded", () => {
    const usernameBtn = document.getElementById("usernameBtn");
    const dropdown = document.getElementById("userDropdown");
    const logoutBtn = document.getElementById("logoutBtn");
    const navMenu = document.getElementById("navMenu");

    function parseJwt(token) {
        try {
            const payload = token.split('.')[1];
            return JSON.parse(atob(payload));
        } catch {
            return null;
        }
    }

    const token = localStorage.getItem("jwt");
    if (!token) {
        alert("Debes iniciar sesión");
        window.location.href = "../index.html";
        return;
    }

    const decoded = parseJwt(token);
    const username = decoded?.sub || decoded?.username || "Usuario";
    usernameBtn.textContent = username;
    logoutBtn.style.display = "block";

    navMenu.innerHTML = `
        <a href="vehiculos.html">Vehículos</a>
        <a href="repuestos.html">Repuestos</a>
        <a href="presupuestos.html">Presupuestos</a>
        <a href="usuarios.html">Usuarios</a>
    `;

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

    // Obtener id del usuario a editar
    const urlParams = new URLSearchParams(window.location.search);
    const usuarioId = urlParams.get("id");
    const formTitle = document.getElementById("formTitle");
    const usuarioForm = document.getElementById("usuarioForm");

    if (!usuarioId) {
        alert("No se especificó el usuario a editar");
        window.location.href = "usuarios.html";
        return;
    }

    // Cargar datos del usuario
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
        .catch(err => alert(err));

    // Guardar cambios
    usuarioForm.addEventListener("submit", (e) => {
        e.preventDefault();

        const updatedUser = {
            username: document.getElementById("username").value,
            firstName: document.getElementById("firstName").value,
            lastName: document.getElementById("lastName").value,
            country: document.getElementById("country").value,
            role: document.getElementById("role").value
        };

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
            .catch(err => alert(err));
    });

    document.getElementById("cancelBtn").addEventListener("click", () => {
        window.location.href = "usuarios.html";
    });

    const logo = document.querySelector(".logo");
    logo.addEventListener("click", () => {
        window.location.href = "../index.html";
    });
});
