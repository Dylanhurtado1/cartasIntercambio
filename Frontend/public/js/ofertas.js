//const { createApp, ref, onMounted } = Vue;

Vue.createApp({
  setup() {
    const ofertas = Vue.ref([]);
    const backendURL = "http://localhost:8080"; 

    function obtenerIdDesdeURL() {
        let url = window.location.href;
        let id = url.match(/\/publicacion\/([a-fA-F0-9]{24})/);
        return id[1];
      }

    async function cargarOfertas() {
      const publicacionId = obtenerIdDesdeURL();
      console.log(`${backendURL}/publicaciones/${publicacionId}/ofertas`)
      try {
        const res = await fetch(`${backendURL}/publicaciones/${publicacionId}/ofertas`);
        if (!res.ok) throw new Error("Error en la carga");
        ofertas.value = await res.json();
      } catch (e) {
        //alert("No se pudieron cargar las ofertas. Serás redirigido.");
        //window.location.href = "/";
        console.log(e)
      }
    }

    async function aceptarOferta(idOferta) {
        console.log(`${backendURL}/ofertas/${idOferta}`)
        try {
          const res = await fetch(`${backendURL}/publicaciones/ofertas/${idOferta}`, {
            method: "PATCH",
            headers: {
              "Content-Type": "application/json-patch+json"
            },
            body: JSON.stringify([
              {
                op: "replace",
                path: "/estado",
                value: "ACEPTADO"
              }
            ])
          });
      
          if (res.ok) {
            alert("Oferta aceptada correctamente");
            cargarOfertas(); // recargar la lista
          } else {
            alert("Error al aceptar la oferta");
          }
        } catch (err) {
          console.error(err);
          alert("Error al conectar con el servidor");
        }
      }
      
      async function rechazarOferta(idOferta) {
        try {
          const res = await fetch(`${backendURL}/publicaciones/ofertas/${idOferta}`, {
            method: "PATCH",
            headers: {
              "Content-Type": "application/json-patch+json"
            },
            body: JSON.stringify([
              {
                op: "replace",
                path: "/estado",
                value: "RECHAZADO"
              }
            ])
          });
      
          if (res.ok) {
            alert("Oferta rechazada");
            cargarOfertas(); // recargar la lista
          } else {
            alert("Error al rechazar la oferta");
          }
        } catch (err) {
          console.error(err);
          alert("Error al conectar con el servidor");
        }
      }

    function formatearFecha(fecha) {
      return new Date(fecha).toLocaleString("es-AR");
    }

    Vue.onMounted(cargarOfertas);

    return {
      ofertas,
      aceptarOferta,
      rechazarOferta,
      formatearFecha
    };
  }
}).mount("#app");
