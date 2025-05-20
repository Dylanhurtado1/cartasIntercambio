Vue.createApp({
  data() {
    return {
      isMenuOpen: false,
      menuItems: [
        { label: "Inicio", href: "/" },
        { label: "Publicar Carta", href: "/publicar" },
        { label: "Usuario", href: "/usuario" },
        { label: "Estadísticas", href: "/estadisticas" }
      ],
      usuarioLogueado: JSON.parse(localStorage.getItem("usuarioLogueado"))
    };
  },
  methods: {
    toggleMenu() {
      this.isMenuOpen = !this.isMenuOpen;
    },
    logout() {
      localStorage.removeItem("usuarioLogueado");
      window.location.href = "/login";
    }
  },
  template: `
    <div>
      <div class="top-header">
        <h1>Cartas Online</h1>
        <button class="menu-toggle" @click="toggleMenu">&#9776;</button>
        <p>Buscá y comprá cartas de Pokémon y Yu-Gi-Oh!</p>
      </div>
      <p v-if="usuarioLogueado" class="usuario-logueado" style="background:#eef;padding:5px;">
         Usuario: {{ usuarioLogueado.user }}
         <button @click="logout">Cerrar sesión</button>
      </p>
      <nav class="menu">
        <ul :class="{ show: isMenuOpen }">
          <li v-for="(item, index) in menuItems" :key="index">
            <a :href="item.href">{{ item.label }}</a>
          </li>
        </ul>
      </nav>
    </div>
  `
}).mount("#menu-app");