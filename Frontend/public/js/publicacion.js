var { createApp, ref } = Vue;

createApp({
  setup() {
    const publicacion = ref(null);
    const oferta = ref({
      precio: null,
      cartasIntercambio: []
    });

    const backendURL = "http:/localhost:8080"; // Reemplazá con tu API real

    function obtenerPublicacion() {
        const id = obtenerIdDesdeURL(); 
        fetch(`${backendURL}/publicaciones/${id}`)
        .then(res => {
          if (!res.ok) throw new Error("Publicación no encontrada");
          return res.json();
        })
        .then(json => {
          publicacion.value = json;
        })
        .catch(err => {
          alert("No se pudo cargar la publicación. Redirigiendo a la página principal...")
          console.error("Error al obtener publicación:", err);
        });
    }

    function obtenerIdDesdeURL() {
      let url = window.location.href;
      // Expresión regular 
      let id = url.match(/\/publicacion\/(\d+)/);
      
      return id[1]; // de por si desde el backend tiene que recibir un "publicacion/:id", no hace verificar si es nulo
      
    }

    function agregarCarta() {
      oferta.value.cartasIntercambio.push({
        nombre: '',
        juego: '',
        estado: '',
        imagen: ''
      });
    }

    function enviarOferta() {
        const tienePrecio = oferta.value.precio && oferta.value.precio > 0;
        const tieneCartaValida = oferta.value.cartasIntercambio.some(carta =>
          carta.nombre.trim() !== '' &&
          carta.juego.trim() !== '' &&
          carta.estado.trim() !== ''
        );
      
        if (!tienePrecio && !tieneCartaValida) {
          alert("Debés ingresar al menos un precio o una carta válida para intercambiar.");
          return;
        }
      
        const id = obtenerIdDesdeURL();
        fetch(`${backendURL}/publicaciones/${id}/ofertas`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(oferta.value)
        })
          .then(res => {
            if (res.ok) {
              alert("Oferta enviada exitosamente");
              oferta.value = { precio: null, cartasIntercambio: [] }; // reset
            } else {
              alert("Hubo un error al enviar la oferta");
            }
          })
          .catch(err => console.error("Error al enviar oferta:", err));
      }
      
    obtenerPublicacion();

    return {
      publicacion,
      oferta,
      agregarCarta,
      enviarOferta
    };
  }
}).mount("#publicacion-app");
