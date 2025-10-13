document.addEventListener("DOMContentLoaded", () => {

    document.getElementById("goLogin").addEventListener("click", () => {
        window.location.href = "formLogin.html";
    });

    const registerForm = document.getElementById("registerForm");

    registerForm.addEventListener("submit", async (evento) => {
        evento.preventDefault();

        const user = {
            username: document.getElementById("username").value,
            lastname: document.getElementById("lastname").value,
            firstname: document.getElementById("firstname").value,
            country: document.getElementById("country").value,
            password: document.getElementById("password").value
        };

        try {
            const response = await fetch("http://localhost:8080/auth/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json"
                },
                body: JSON.stringify(user)
            });

            if (response.ok) {
                alert("Registro exitoso, ahora inicia sesión.");
                window.location.href = "formLogin.html";
            } else {
                let errorMessage = "Error al registrar";
                try {
                    const errorData = await response.json();
                    errorMessage = errorData?.message || errorData || errorMessage;
                } catch (_) {}
                alert(errorMessage);
            }
        } catch (error) {
            alert("Error de conexión: " + error);
        }
    });

});