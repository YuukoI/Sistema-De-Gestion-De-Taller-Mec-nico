document.getElementById("signUp").addEventListener("click", () => {
    window.location.href = "formRegister.html";
});

document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const login = document.getElementById("login").value.trim();
    const password = document.getElementById("password").value.trim();

    if (!login || !password) {
        const errorDiv = document.getElementById("loginError");
        if (errorDiv) {
            errorDiv.textContent = "Ingrese usuario y contraseña";
        } else {
            alert("Ingrese usuario y contraseña");
        }
        return;
    }

    try {
        const response = await fetch("http://localhost:8080/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ username: login, password: password })
        });

        if (!response.ok) {
            const errorDiv = document.getElementById("loginError");
            if (response.status === 403 || response.status === 401) {
                if (errorDiv) {
                    errorDiv.textContent = "No existe el usuario o la contraseña es incorrecta";
                } else {
                    alert("No existe el usuario o la contraseña es incorrecta");
                }
            } else {
                if (errorDiv) {
                    errorDiv.textContent = "Error inesperado: " + response.status;
                } else {
                    alert("Error inesperado: " + response.status);
                }
            }
            return;
        }

        const data = await response.json();
        const token = data.token;
        localStorage.setItem("jwt", token);
        window.location.href = "../index.html";

    } catch (error) {
        const errorDiv = document.getElementById("loginError");
        if (errorDiv) {
            errorDiv.textContent = "No se pudo conectar con el servidor";
        } else {
            alert("No se pudo conectar con el servidor");
        }
    }
});
