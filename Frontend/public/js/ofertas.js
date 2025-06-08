const { createApp, onMounted, nextTick, watch } = Vue;
import {sesionAbierta, obtenerDatoCrudo, obtenerDatoObjeto, manejarErrorImagen, obtenerURL, ejecutarSliderVanilla} from './utils.js'
//import {ejecutarSliderVanilla} from './sliderVanilla.js'


createApp({
  setup() {
    /*if(!sesionAbierta())
      window.location.href = "/login"*/
    const explicacion = Vue.ref("")
    const usuarioEsElPublicador = Vue.ref(true) //asumo muy potentemente que si
    const ofertas = Vue.ref([]);
    const publicacionDeOrigen = Vue.ref(null)
    const backendURL = obtenerURL(); 

    function obtenerIdDesdeURL() {
      let url = window.location.href;
      let id = url.match(/\/publicacion\/([a-fA-F0-9]{24})/);
      return id[1];
    }

    async function validarPublicador(){
      if(!sesionAbierta()) //si la sesión no está abierta, directo le dice al usuario que se logue/registre para entrar
        window.location.href = "/login"

      try {
        const res = await fetch(`${backendURL}/publicaciones/${obtenerIdDesdeURL()}`);
        if (!res.ok) throw new Error("Error en la carga de publicacion");
        const data = await res.json();
        const id = data.publicador.id
        usuarioEsElPublicador.value = (id == obtenerDatoObjeto("usuarioActual").id)
        if(!usuarioEsElPublicador.value){
          explicacion.value = "Usted no es el dueño de la publicación, vuelva al inicio"
          return
        }

        publicacionDeOrigen.value = data;
        
        cargarOfertas()
          //window.location.href = "/"
        

      } catch(e){
        console.log(e)
        //ante un error del servidor, que se vaya al index y listo el pollo y la gallina 
        //window.location.href = "/"
        explicacion.value = e
      }
    }

    async function cargarOfertas() {
      const publicacionId = obtenerIdDesdeURL();
      try {
        const res = await fetch(`${backendURL}/publicaciones/${publicacionId}/ofertas`);
        if (!res.ok) throw new Error("Error en la carga de ofertas");
        const data = await res.json();
        ofertas.value = data;
   
        if(ofertas.value.length === 0)  
          explicacion.value = "No hay ofertas aún para esta publicación."
      } catch (e) {
        console.log(e)
      }
    }

    async function aceptarOferta(idOferta) {
      console.log(`${backendURL}/ofertas/${idOferta}`)
      try {
        const res = await fetch(`${backendURL}/publicaciones/ofertas/${idOferta}`, {
          method: "PATCH",
          headers: {
            "Content-Type": "application/json-patch+json",
            "Authorization": "Bearer " + obtenerDatoCrudo("jwt")
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
            "Content-Type": "application/json-patch+json",
            "Authorization": "Bearer " + obtenerDatoCrudo("jwt")
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
      return new Date(fecha).toLocaleString("es-ES")
    }

    function ofertaContieneCarta(oferta, carta) {
      const cartasOfrecidasDeOferta = oferta.cartasOfrecidas;
      return !cartasOfrecidasDeOferta.some(cartaOferta => JSON.stringify(cartaOferta) === JSON.stringify(carta))
    }

    onMounted(async () => {
      await validarPublicador()
      await nextTick();
      //ejecutarSliderVanilla(); no me funciona ;(

    });

    watch(ofertas, async (nuevasOfertas) => {
      if (nuevasOfertas.length) {
        await nextTick();
        ejecutarSliderVanilla();
      }
    });

    
    return {
      publicacionDeOrigen,
      ofertaContieneCarta,
      ofertas,
      aceptarOferta,
      rechazarOferta,
      formatearFecha,
      usuarioEsElPublicador, 
      explicacion,
      manejarErrorImagen
    };
  }
}).mount("#app");
