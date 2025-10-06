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
        usernameBtn.textContent = username;
        logoutBtn.style.display = "block";
        esAdmin = decoded?.sub === "ADMIN";

        // Navbar dinámico según rol
        let navHtml = `
            <a href="html/vehiculos.html">Vehículos</a>
            <a href="html/repuestos.html">Repuestos</a>
            <a href="html/presupuestos.html">Presupuestos</a>
        `;
        if (esAdmin) {
            navHtml += `<a href="html/usuarios.html">Usuarios</a>`;
        }
        navMenu.innerHTML = navHtml;

    } else {
        usernameBtn.textContent = "Acceder";
        logoutBtn.style.display = "none";
        navMenu.innerHTML = "";
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
        window.location.reload();
    });

    document.addEventListener("click", (e) => {
        if (!usernameBtn.contains(e.target) && !dropdown.contains(e.target)) {
            dropdown.style.display = "none";
        }
    });
});

// Función global para copiar texto
function copiarTexto(texto) {
    navigator.clipboard.writeText(texto).then(() => {
        alert(`Copiado: ${texto}`);
    }).catch(() => {
        alert('Error al copiar');
    });
}