document.addEventListener("DOMContentLoaded", () => {
    const usernameBtn = document.getElementById("usernameBtn");
    const dropdown = document.getElementById("userDropdown");
    const logoutBtn = document.getElementById("logoutBtn");
    const navMenu = document.getElementById("navMenu");

    let userRole = null;
    const token = localStorage.getItem("jwt");

    function parseJwt(token) {
        try {
            return JSON.parse(atob(token.split('.')[1]));
        } catch (e) {
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
    userRole = decoded?.rol || "USER";

    usernameBtn.textContent = username;
    logoutBtn.style.display = "block";

    let navHtml = `
        <a href="vehiculos.html">Vehículos</a>
        <a href="repuestos.html">Repuestos</a>
        <a href="presupuestos.html">Presupuestos</a>
    `;
    if (userRole === "ADMIN") {
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

    const urlParams = new URLSearchParams(window.location.search);
    const usuarioId = urlParams.get("id");
    const formTitle = document.getElementById("formTitle");
    const usuarioForm = document.getElementById("usuarioForm");

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

            if (userRole !== "ADMIN") {
                document.getElementById("role").disabled = true;
            }
        })
        .catch(err => alert(err));

    usuarioForm.addEventListener("submit", (e) => {
        e.preventDefault();

        if (userRole !== "ADMIN") {
            alert("No tienes permisos para editar usuarios");
            return;
        }

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
