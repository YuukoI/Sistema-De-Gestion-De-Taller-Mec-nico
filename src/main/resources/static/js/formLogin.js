document.getElementById("signUp").addEventListener("click", () => {
    window.location.href = "formRegister.html";
});

document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const login = document.getElementById("login").value;
    const password = document.getElementById("password").value;

    const response = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ username: login, password: password })
    });

    if (!response.ok) {
        alert("Login fallido: " + response.status);
        return;
    }

    const data = await response.json();
    const token = data.token;

    localStorage.setItem("jwt", token);

    alert("Login exitoso!");
    window.location.href = "../index.html";
});