// Sinceramente, esto es una prueba para ver como era el tema de usar componentes.
// La próxima uso vue cli/vue router y no me vuelvo loco haciendo "lo fácil y sencillo" MUY ENTRE COMILLAS
// PD: muy tarde para refactorear en todo el proyecto, no lloren por mi, ya estoy muerto.

export const ImageUpload = {
  props: ["imagenes"], // props del componente
  emits: ["borrar-todas"], //métodos que se les puede pasar
  computed: {
    previews() {
      return this.imagenes.map((file) => ({
        name: file.name,
        preview: URL.createObjectURL(file),
      }));
    },
  },
  template: `
    <div class="image-upload">
      <div v-show="imagenes.length" class="preview-container">
        <div class="preview" v-for="(img, index) in previews" :key="index">
          <img :src="img.preview" :alt="img.name" />
        </div>
        <button type="button" class="borrar-todas" @click="$emit('borrar-todas')">
          Borrar las imágenes
        </button>
      </div>
    </div>
  `,
};
