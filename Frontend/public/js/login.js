import {guardarDatoCrudo, guardarDatoObjeto, obtenerURL} from './utils.js'
const { onMounted } = Vue;

Vue.createApp({
  setup() {
    const user = Vue.ref("")
    const password = Vue.ref("")
    const error = Vue.ref("")
    const backendURL = obtenerURL(); 


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
