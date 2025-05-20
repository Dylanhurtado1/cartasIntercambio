const backendURL = "http://localhost:8080";

const usuarioLogueado = JSON.parse(localStorage.getItem("usuarioLogueado"));
if (usuarioLogueado) {
  window.location.href = "/";
}

document.getElementById('signupForm').addEventListener('submit', function(e) {
  e.preventDefault();
  const data = {
    user: document.getElementById('user').value,
    nombre: document.getElementById('nombre').value,
    correo: document.getElementById('correo').value,
    password: document.getElementById('password').value
  };

  fetch(backendURL + "/usuarios", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data)
  })
  .then(res => {
    if (!res.ok) throw new Error("Registro fallido");
    return res.json();
  })
  .then(json => {
    document.getElementById('mensaje').textContent = "Usuario creado correctamente. Ahora podés iniciar sesión.";
    // Automáticamente podrías redirigir a /login.html:
    window.location.href = "/";
  })
  .catch(err => { document.getElementById('mensaje').textContent = err });
});