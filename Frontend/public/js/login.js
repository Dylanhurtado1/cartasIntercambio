const backendURL = "http://localhost:8080";

document.getElementById('loginForm').addEventListener('submit', function(e) {
  e.preventDefault();
  const data = {
    user: document.getElementById('user').value,
    password: document.getElementById('password').value
  };

  fetch(backendURL + "/usuarios/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data)
  })
  .then(res => {
    if (!res.ok) throw new Error("Usuario o contraseña incorrectos");
    return res.json();
  })
  .then(response => {
    // Guardá el token y usuario en localStorage
    localStorage.setItem("jwt", response.token);
    localStorage.setItem("usuarioLogueado", JSON.stringify(response.usuario));
    window.location.href = "/"; // Redirige a la página principal
  })
  .catch(err => {
    document.getElementById('mensaje').textContent = err.message;
  });
});