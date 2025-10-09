document.addEventListener("DOMContentLoaded", () => {
    const token = localStorage.getItem("jwt");

    if (!token) {
        window.location.href = "../html/formLogin.html";
        return;
    }

    const payloadBase64 = token.split(".")[1];
    try {
        const payloadJson = JSON.parse(atob(payloadBase64));
        const exp = payloadJson.exp;

        const now = Math.floor(Date.now() / 1000);

        if (exp && now > exp) {
            localStorage.removeItem("token");
            window.location.href = "../html/formLogin.html";
        }
    } catch (e) {
        console.error("Error al decodificar el token:", e);
        localStorage.removeItem("token");
        window.location.href = "/login.html";
    }
});
