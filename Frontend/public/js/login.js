import {guardarDatoCrudo, guardarDatoObjeto} from './datos.js'
const { onMounted } = Vue;

Vue.createApp({
  setup() {
    const user = Vue.ref("")
    const password = Vue.ref("")
    const error = Vue.ref("")
    const backendURL = "http://localhost:8080"; 


    function handleLogin() {
        fetch(backendURL + "/usuarios/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ user: user.value, password: password.value })
        })
            .then(res => {
                if (!res.ok) throw new Error("Credenciales incorrectas")
                return res.json()
            })
            .then(response => {
                /*localStorage.setItem("jwt", response.token);
                localStorage.setItem("usuarioActual", JSON.stringify(response.usuario));*/
                guardarDatoCrudo("jwt", response.token)
                guardarDatoObjeto("usuarioActual", response.usuario)
                window.location.href = "/usuario"; // Redirige a la página principal
            })
            .catch(err => error.value = err.message)
    }

    onMounted(() => {
      if (localStorage.getItem("jwt")) 
        window.location.href = "/usuario";
    });

    return { user, password, error, handleLogin }
  }
}).mount("#loginApp")
