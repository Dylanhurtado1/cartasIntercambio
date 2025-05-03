//let { createApp } = Vue;

Vue.createApp({
  data() {
    return {
      isMenuOpen: false,
      menuItems: [
        { label: "Inicio", href: "/" },
        { label: "Publicar Carta", href: "/publicar" },
        { label: "Mi Cuenta", href: "/cuenta" },
        { label: "Favoritos", href: "/favoritos" }
      ]
    };
  },
  methods: {
    toggleMenu() {
      this.isMenuOpen = !this.isMenuOpen;
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
          <li v-for="(item, index) in menuItems" :key="index">
            <a :href="item.href">{{ item.label }}</a>            
          </li>
        </ul>
      </nav>
    </div>
  `
}).mount("#menu-app");