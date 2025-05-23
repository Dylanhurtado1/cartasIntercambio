function onGoogleSignIn(response) {
    const id_token = response.credential;
    // Llamá al backend para intercambiar el id_token por tu JWT propio
    fetch("http://localhost:8080/usuarios/oauth2/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ id_token })
    })
    .then(res => res.json())
    .then(data => {
        if (data.token) {
            // Guardá el JWT y usuario igual que en el login normal
            localStorage.setItem("jwt", data.token);
            localStorage.setItem("usuarioLogueado", JSON.stringify(data.usuario));
            window.location.href = "/"; // O donde quieras redirigir logueados
        } else {
            document.getElementById('mensaje').textContent = data.error || "Error en login con Google";
        }
    })
    .catch(err => {
        document.getElementById('mensaje').textContent = "Error en login con Google";
        console.error(err);
    });
}