const { onMounted, nextTick, watch} = Vue;
import {obtenerDatoObjeto, obtenerDatoCrudo, sesionAbierta, manejarErrorImagen, obtenerURL, ejecutarSliderVanilla} from './utils.js'
//import {ejecutarSliderVanilla} from './sliderVanilla.js'


Vue.createApp({
  setup() {
    const publicacion = Vue.ref(null)
    const formVisible = Vue.ref(false)
    const esUsuarioOriginal = Vue.ref(false)
    const backendURL = obtenerURL()


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

        const idActual = obtenerDatoObjeto("usuarioActual")?.id;
        const sesionValida = sesionAbierta() && idActual != publicacion.value.publicador.id
        formVisible.value = (publicacion.value.estado != "FINALIZADA") && sesionValida 
        esUsuarioOriginal.value = (idActual == publicacion.value.publicador.id)

      } catch (error) {
        window.location.href = "/";
      }
    }

    function obtenerIdDesdeURL() {
      let url = window.location.href;
      // Expresión regular 
      let id = url.match(/\/publicacion\/([a-fA-F0-9]{24})/);
      
      return id[1]; // de por si desde el backend tiene que recibir un "publicacion/:id", no hace verificar si es nulo
    }
    

    /* OFERTA */

    const formSubmitted = Vue.ref(false); // Para mostrar el mensaje después del envío
    const nuevaOferta = Vue.ref({
      precio: null,
      cartasSeleccionadas: []
    });

    // Validación del formulario: El formulario es válido si hay precio o cartas
    const isValid = () => {
      return nuevaOferta.value.precio || nuevaOferta.value.cartasSeleccionadas.length > 0;
    };



    // Función para enviar el formulario
    const submitForm = () => {
      if(isValid()){
        const cartasSeleccionadas = nuevaOferta.value.cartasSeleccionadas.map(index =>
          publicacion.value.cartasInteres[index] // obtengo las cartas en base al índice del select multiple
        );

        const oferta = {
          fecha: new Date().toJSON(), 
          idPublicacion: obtenerIdDesdeURL(),
          monto: (price.value == null) ? 0 : price.value,
          cartasOfrecidas: cartasSeleccionadas,
          ofertante: {
            id: null,
            user: null
          }
        }

        console.log(oferta)
        formSubmitted.value = true; // Mostrar el mensaje después de enviar
        console.log(JSON.stringify(oferta))
  
        fetch(backendURL + "/publicaciones/" + oferta.idPublicacion + "/ofertas", {
          method: "POST",
          body: JSON.stringify(oferta),
          headers: {
            "Content-type": "application/json; charset=UTF-8",
            "Authorization": "Bearer " + obtenerDatoCrudo("jwt")
          }
        })
        .then(response => response.json()) 
        .then(json => {
            console.log(json)
            window.location.href = '/usuario'
        })
        .catch(err => {
            console.log(err)
            alert('Error con el servidor')
            return;
        })
      } 
      else
        alert("Hay campos inválidos")
    };    

    onMounted(async () => {
        await cargarPublicacion(); // los hacks malvados que hago para no refactorizar todo las vistas que tienen imágenes JAJAJA
        await nextTick();
        ejecutarSliderVanilla();
    });

    return {
      //VISUALIZACIÓN DE LA PUBLICACIÓN
      publicacion,
      formatearFecha,
      esUsuarioOriginal,
      // FORMULARIO OFERTAS
      formVisible,
      formSubmitted,
      isValid,
      submitForm,
      nuevaOferta,
      manejarErrorImagen
    };
  }
}).mount("#app");

window.addEventListener('DOMContentLoaded', () => {
  ejecutarSliderVanilla();
});