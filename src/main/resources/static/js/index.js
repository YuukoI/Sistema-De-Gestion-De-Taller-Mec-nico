document.addEventListener("DOMContentLoaded", () => {
    const usernameBtn = document.getElementById("usernameBtn");
    const dropdown = document.getElementById("userDropdown");
    const logoutBtn = document.getElementById("logoutBtn");
    const navMenu = document.getElementById("navMenu");

    const presupuestoMesCard = document.getElementById("presupuestos-mes");
    const presupuestoSemanaCard = document.getElementById("presupuestos-semana");
    const ingresosMesCard = document.getElementById("ingresos-mes");
    const ingresosSemanaCard = document.getElementById("ingresos-semana");

    const formatoMoneda = new Intl.NumberFormat('es-AR', {
        style: 'currency',
        currency: 'ARS',
        minimumFractionDigits: 2
    });

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

        esAdmin = decoded?.rol === "ADMIN";

        let navHtml = `
            <a href="html/vehiculos.html">Vehículos</a>
            <a href="html/repuestos.html">Repuestos</a>
            <a href="html/presupuestos.html">Presupuestos</a>
        `;
        if (esAdmin) {
            navHtml += `<a href="html/usuarios.html">Usuarios</a>`;
            navHtml += `<a href="html/logs.html">Auditoría</a>`;
        }
        navMenu.innerHTML = navHtml;

        fetchResumenPresupuestos(token);
        fetchIngresos(token);

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
        window.location.href = "html/formLogin.html";
    });

    document.addEventListener("click", (e) => {
        if (!usernameBtn.contains(e.target) && !dropdown.contains(e.target)) {
            dropdown.style.display = "none";
        }
    });

    function fetchResumenPresupuestos(token) {
        fetch("http://localhost:8080/presupuestos/resumen", {
            headers: { "Authorization": "Bearer " + token }
        })
            .then(res => {
                if (!res.ok) throw new Error("Error al obtener resumen de presupuestos");
                return res.json();
            })
            .then(data => {
                presupuestoMesCard.textContent = data.mes || 0;
                presupuestoSemanaCard.textContent = data.semana || 0;
            })
            .catch(err => console.error(err));
    }

    function fetchIngresos(token) {
        fetch("http://localhost:8080/presupuestos/ingresos", {
            headers: { "Authorization": "Bearer " + token }
        })
            .then(res => {
                if (!res.ok) throw new Error("Error al obtener ingresos");
                return res.json();
            })
            .then(data => {
                ingresosMesCard.textContent = formatoMoneda.format(data.mes || 0);
                ingresosSemanaCard.textContent = formatoMoneda.format(data.semana || 0);

                mostrarGraficoIngresos(data.mes, data.semana);
            })
            .catch(err => console.error(err));
    }

    function mostrarGraficoIngresos(dataMes, dataSemana) {
        const ctxMes = document.getElementById("graficoMes").getContext("2d");
        const ctxSemana = document.getElementById("graficoSemana").getContext("2d");

        new Chart(ctxMes, {
            type: "bar",
            data: {
                labels: ["Ingresos Mes"],
                datasets: [{
                    label: "Ingresos en $",
                    data: [dataMes],
                    backgroundColor: ["#4e73df"]
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { display: false },
                    tooltip: { callbacks: {
                            label: function(context) {
                                return `$ ${context.raw.toLocaleString()}`;
                            }
                        }}
                },
                scales: {
                    y: { beginAtZero: true }
                }
            }
        });

        new Chart(ctxSemana, {
            type: "bar",
            data: {
                labels: ["Ingresos Semana"],
                datasets: [{
                    label: "Ingresos en $",
                    data: [dataSemana],
                    backgroundColor: ["#1cc88a"]
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { display: false },
                    tooltip: { callbacks: {
                            label: function(context) {
                                return `$ ${context.raw.toLocaleString()}`;
                            }
                        }}
                },
                scales: {
                    y: { beginAtZero: true }
                }
            }
        });
    }

    function copiarTexto(texto) {
        navigator.clipboard.writeText(texto)
            .then(() => alert(`Copiado: ${texto}`))
            .catch(() => alert('Error al copiar'));
    }
});
