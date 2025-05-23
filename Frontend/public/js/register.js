Vue.createApp({
  setup() {
    const user = Vue.ref("")
    const nombre = Vue.ref("")
    const email = Vue.ref("")
    const password = Vue.ref("")
    const error = Vue.ref("")
    const backendURL = "http://localhost:8080"; 


    function handleRegister() {
        const data = {
            user: user.value,
            nombre: nombre.value,
            correo: email.value,
            password: password.value
        }

        console.log(data)

        fetch(backendURL + "/usuarios", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(data)
        })
        .then(res => {
          if (!res.ok) throw new Error("Registro fallido");
          return res.json();
        })
        .then(() => window.location.href = "/login")
        .catch(err => error.value = err.message)
    }

    return { user, nombre, email, password, error, handleRegister }
  }
}).mount("#registerApp")
