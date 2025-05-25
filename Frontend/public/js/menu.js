import {obtenerDatoCrudo, vaciarDatos} from './datos.js'

Vue.createApp({
  data() {
    return {
      sesionCerrada: (obtenerDatoCrudo("jwt") === null),
      isMenuOpen: false,
      menuItems: [
        { label: "Inicio", href: "/", public: true },
        { label: "Publicar Carta", href: "/publicar", public: false},
        { label: "Usuario", href: "/usuario", public: false },
        { label: "Estadísticas", href: "/estadisticas", public: false }
      ]
    };
  },
  methods: {
    toggleMenu() {
      this.isMenuOpen = !this.isMenuOpen;
    },
    cerrarSesion(){
      vaciarDatos()
      window.location.href = "/"
    }
  },
  template: `
    <div>
      <div class="top-header">
        <h1>Cartas Online</h1>
        <button class="menu-toggle" @click="toggleMenu">&#9776;</button>
        <p>Buscá y comprá cartas de Pokémon y Yu-Gi-Oh!</p>
      </div>
      <nav class="menu">
        <ul :class="{ show: isMenuOpen }">
          <li v-for="(item, index) in menuItems.filter(i => i.public || (!i.public && !sesionCerrada)) " :key="index">
            <a :href="item.href">{{ item.label }}</a>            
          </li>
          
          <li v-show="sesionCerrada">
            <a href="/login">Iniciar sesión</a>            
          </li>

          <li v-show="sesionCerrada">
            <a href="/registrar">Registrarse</a>            
          </li>

          <li v-show="!sesionCerrada" @click="cerrarSesion">
            <a href="#">Cerrar sesión</a>            
          </li>
        </ul>
      </nav>
    </div>
  `
}).mount("#menu-app");