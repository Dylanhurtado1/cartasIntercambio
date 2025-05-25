const { createApp, ref, onMounted } = Vue;
import {sesionAbierta} from './datos.js'

createApp({
  setup() {
    const mostrar = ref(false);
    const texto = ref("Para continuar necesitás iniciar sesión o registrarte.");
    onMounted(() => {
      if (!sesionAbierta()) {
        const el = document.getElementById("user-certification");
        if (el?.dataset?.texto) texto.value = el.dataset.texto; //trucazo por si quiero settear un mensaje
        mostrar.value = true;
      }
    });
    return { mostrar, texto };
  },
  template: `
  <div v-if="mostrar" class="user-cert">
    <h2> ERROR </h2>
    <p>{{ texto }}</p>
        <a href="/login">Iniciar sesión</a>
        <a href="/registrar">Registrarse</a>
    </div>
`,
}).mount("#user-certification");
