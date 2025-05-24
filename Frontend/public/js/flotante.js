import {obtenerDatoObjeto, vaciarDatos} from './datos.js'
const { createApp, ref, onMounted } = Vue;

createApp({
  setup() {
    const usuario = ref("Cargando...");

    const cerrarSesion = () => {
        vaciarDatos()
        window.location.href = "/login"
    };
    onMounted(() => {
      const usuarioActual = obtenerDatoObjeto("usuarioActual");
      usuario.value = usuarioActual
    });
    return { usuario, cerrarSesion };
  },
  template: `
    <div id=sesion v-if="usuario">
        <div>Usuario actual: {{ usuario.user }}</div>
        <button v-if="usuario" v-on:click="cerrarSesion">Cerrar sesión</button>
    </div>
    `,
}).mount("#datos-f");
