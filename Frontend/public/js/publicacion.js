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
    

    /* OFERTA */

    const price = Vue.ref(null); // Precio de oferta
    const cartasOfrecidas = Vue.ref([]); 
    const formSubmitted = Vue.ref(false); // Para mostrar el mensaje después del envío

    // Validación del formulario: El formulario es válido si hay precio o cartas
    const isValid = () => {
      return price.value || cartasOfrecidas.value.length > 0;
    };

    // Función para agregar una carta
    const addCard = () => {
      cartasOfrecidas.value.push({
        nombre: '',
        juego: '',
        estado: 'nuevo',
        imagenes: []
      });
    };

    // Función para manejar el cambio de imagen
    function handleFileChange(index, event) {
      const files = event.target.files;
      if (!files.length) return;
    
      // Guardamos solo los nombres
      cartasOfrecidas.value[index].imagenes = Array.from(files).map(file => file.name);
    }

    const deleteCard = (index) =>
    {
      cartasOfrecidas.value.splice(index, 1);
    }

    // Función para enviar el formulario
    const submitForm = () => {
      const oferta = {
        fecha: new Date().toJSON(), 
        idPublicacion: obtenerIdDesdeURL(),
        monto: (price.value == null) ? 0 : price.value,
        cartasOfrecidas: JSON.parse(JSON.stringify(cartasOfrecidas.value)), //hermosura, el objeto es hermoso
        ofertante: {
          id: 1,
          user: "minombre" //por ahora hardcodeado
        }
      }
      console.log(oferta)
      formSubmitted.value = true; // Mostrar el mensaje después de enviar
      console.log(JSON.stringify(oferta))

      fetch(backendURL + "/publicaciones/" + oferta.idPublicacion + "/ofertas", {
        method: "POST",
        body: JSON.stringify(oferta),
        headers: {"Content-type": "application/json; charset=UTF-8"}
      })
      .then(response => response.json()) 
      .then(json => {
          console.log(json)
          //window.location.href = '../'
      })
      .catch(err => {
          console.log(err)
          alert('Error con el servidor')
      })

    };

    

    onMounted(() => {
      cargarPublicacion();
    });

    return {
      publicacion,
      formatearFecha,
      price,
      cards: cartasOfrecidas,
      formSubmitted,
      isValid,
      addCard,
      handleFileChange,
      submitForm,
      deleteCard
    };
  }
}).mount("#app");
