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
  .then(user => {
    localStorage.setItem("usuarioLogueado", JSON.stringify(user));
    window.location.href = "/"; // Redirige a la página principal o donde quieras
  })
  .catch(err => {
    document.getElementById('mensaje').textContent = err.message;
  });
});