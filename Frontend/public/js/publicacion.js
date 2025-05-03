const { onMounted } = Vue;
Vue.createApp({
  setup() {
    const publicacion = Vue.ref(null);
    const backendURL = "http://localhost:8080"


    function formatearFecha(fechaISO) {
      return new Date(fechaISO).toLocaleDateString("es-AR");
    }

    async function cargarPublicacion() {
      const id = obtenerIdDesdeURL();
      try {
        console.log(`${backendURL}/publicaciones/${id}`)
        const res = await fetch(`${backendURL}/publicaciones/${id}`);
        if (!res.ok) throw new Error("No se pudo obtener la publicación");

        const data = await res.json();
        publicacion.value = data;
      } catch (error) {
        alert("La publicación no existe o el servidor no responde.");
        window.location.href = "/";
      }
    }

    function obtenerIdDesdeURL() {
      let url = window.location.href;
      // Expresión regular 
      let id = url.match(/\/publicacion\/(\d+)/);
      
      return id[1]; // de por si desde el backend tiene que recibir un "publicacion/:id", no hace verificar si es nulo
    }

    onMounted(() => {
      cargarPublicacion();
    });

    return {
      publicacion,
      formatearFecha
    };
  }
}).mount("#app");
