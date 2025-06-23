import {obtenerDatoCrudo, getUserData, sesionAbierta, manejarErrorImagen, obtenerURL} from './utils.js'
const { onMounted, watch } = Vue
import { Pagination } from "./paginacion.js"

Vue.createApp({
  components: { Pagination },
  setup() {
    const usuarioActual = getUserData() || "Invitado"
    const usuario = Vue.ref(usuarioActual);
    const publicacionesCargadas = Vue.ref([]);
    const ofertas = Vue.ref([]);
    const ofertasRecibidas = Vue.ref([]);

    // Datos para paginación, arrepentido de no hacerlo un objeto y listo
    const paginaActual = Vue.ref(0)
    const paginasTotales = Vue.ref(0)
    const publicacionesTotales = Vue.ref(0)
    const contenidoCargado = Vue.ref(0) // 0 = cargando, 1 = cargó, 2 = error, los enums son para débiles

    const id = getUserData().id
    const backendURL = obtenerURL(); 

    async function fetchDatos() {
      contenidoCargado.value = 0
      try {
        const data = {
          credentials: 'include'
        }
 
        const URL = paginaActual.value == 0 ? `${backendURL}/publicaciones/usuario/${id}` : `${backendURL}/publicaciones/usuario/${id}?pageNo=${paginaActual.value}`

        // Obtener publicaciones
        const pubRes = await fetch( URL, data )
        if (pubRes.ok) {
          const contenido = await pubRes.json()
          
          contenidoCargado.value = 1

          publicacionesTotales.value = contenido.totalElements
          paginaActual.value = contenido.pageNo
          paginasTotales.value = contenido.totalPages
          publicacionesCargadas.value = contenido.content
        }

        // Obtener ofertas hechas por el usuario
        const ofertasMias = await fetch(`${backendURL}/publicaciones/usuario/${id}/ofertas/realizadas`, data)
        if (ofertasMias.ok) {
          ofertas.value = await ofertasMias.json()
        }

        // Obtener ofertas hacia el usuario (ofertas que le hicieron a mis publicaciones)
        const ofertasRecibidas = await fetch(`${backendURL}/publicaciones/usuario/${id}/ofertas/recibidas`,  data)
        if (ofertasRecibidas.ok) {
          ofertasRecibidas.value = await ofertasRecibidas.json()
        }

        // le agrego el atributo para saber si la publicación tiene ofertas para ver
        (publicacionesCargadas.value).forEach(publicacion => {
          publicacion.estaOfertado = (ofertasRecibidas.value).some(oferta => 
                                      oferta.idPublicacion == publicacion.id && oferta.estado == "PENDIENTE")
        })

      } catch (err) {
        contenidoCargado.value = 2

        publicacionesCargadas.value = []
        ofertas.value = []

        publicacionesTotales.value = 0
        paginaActual.value = 0
        paginasTotales.value = 0 
        console.error("Error obteniendo datos:", err)
      }
    }

    onMounted(() => {
      if (sesionAbierta()) 
        fetchDatos()
    });

    watch(paginaActual, fetchDatos)

    return { 
      usuario, publicacionesCargadas, ofertas, manejarErrorImagen, 
      paginaActual, paginasTotales, publicacionesTotales, contenidoCargado
    }
  }
}).mount("#app");
