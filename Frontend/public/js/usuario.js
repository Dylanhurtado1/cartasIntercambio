import {obtenerDatoCrudo, getUserData, sesionAbierta, manejarErrorImagen, obtenerURL} from './utils.js'
const { onMounted } = Vue;

Vue.createApp({
  setup() {
    const usuarioActual = getUserData() || "Invitado"
    const usuario = Vue.ref(usuarioActual);
    const publicaciones = Vue.ref([]);
    const ofertas = Vue.ref([]);

    const id = getUserData().id
    const backendURL = obtenerURL(); 

    console.log("Mi usuario actual: " + usuario.value)


    async function fetchDatos() {

      try {
        const headers = {
          "Authorization": `Bearer ${obtenerDatoCrudo("jwt")}`
        };

        // Obtener publicaciones
        const pubRes = await fetch(`${backendURL}/publicaciones/usuario/${id}`, { headers });
        if (pubRes.ok) {
          publicaciones.value = await pubRes.json();
        }

        // Obtener ofertas
        const ofertasRes = await fetch(`${backendURL}/publicaciones/usuario/${id}/ofertas/realizadas`, { headers });
        if (ofertasRes.ok) {
          ofertas.value = await ofertasRes.json();
        }

      } catch (err) {
        console.error("Error obteniendo datos:", err);
      }
    }

    onMounted(() => {
      if (sesionAbierta()) 
        fetchDatos()
    });

    return { usuario, publicaciones, ofertas, manejarErrorImagen };
  }
}).mount("#app");
