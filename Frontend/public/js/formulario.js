const { createApp, ref, onMounted, reactive } = Vue;

import { ImageUpload } from "./imagenesBonitas.js";

import {
  obtenerDatoCrudo,
  sesionAbierta,
  obtenerURL,
  getUserData
} from "./utils.js";

const backendURL = obtenerURL();

const usuarioActual = getUserData()//obtenerDatoObjeto("usuarioActual");

createApp({
  components: {
    ImageUpload,
  },
  setup() {
    // variables reactivas
    const sesionAbiertaValue = ref(sesionAbierta()); // Asume que sesionAbierta() viene de un helper global
    const formDisable = ref(false);
    const form = reactive({
      nombreCarta: "",
      nombreJuego: "",
      descripcion: "",
      estado: "",
      precio: "",
      imagenesCarta: [],
      cartasDeInteres: [],
    });

    const imagenPublicacion = ref(null);

    const jsonResultado = ref("");

    const usuario = ref(usuarioActual || { id: null, user: null });

    onMounted(() => {
      console.log("FUNCA");
      // if (!sesionAbiertaValue.value) window.location.href = "/";
    });

    // métodos 
    const onMainImagesChange = (event) => {
      form.imagenesCarta = Array.from(event.target.files);
    };

    const resetImages = () => {
      form.imagenesCarta = []
      if (imagenPublicacion.value) {
        imagenPublicacion.value.value = "";
      }
    };

    const resetImagesOfertas = (index) => {
      form.cartasDeInteres[index].imagenes = []
    };    

    const formatearTextoDeInput = (array) => {
      // Podía hacer un if de si es un elemento o muchos y simplemente devolver 2 strings por dos ramas, 
      // pero donde está el sufrimiento en esa boludez?
      return `¡${array.length > 1 ? array.length : 'Imagen'} ${array.length > 1 ? 'imágenes' : ''} seleccionada${array.length > 1 ? 's' : ''}!`
    }

    const onInterestImagesChange = (event, index) => {
      form.cartasDeInteres[index].imagenes = Array.from(event.target.files);
    };

    const agregarCartaInteres = () => {
      form.cartasDeInteres.push({
        nombre: "",
        juego: "",
        estado: "",
        imagenes: [],
      });
    };

    const eliminarCartaInteres = (index) => {
      form.cartasDeInteres.splice(index, 1);
    };

    const eliminarTodasCartasInteres = () => {
      form.cartasDeInteres = [];
    };

    const submitForm = () => {
      if (form.imagenesCarta.length === 0) {
        alert("Debes subir al menos una imagen de la carta en venta.");
        return;
      }

      if ((parseFloat(form.precio) <= 0 || isNaN(parseFloat(form.precio))) &&
        form.cartasDeInteres.length === 0) 
      {
        alert("Debes ingresar un precio válido o al menos una carta de interés.");
        return;
      }

      for (let i = 0; i < form.cartasDeInteres.length; i++) {
        if (form.cartasDeInteres[i].imagenes.length === 0) {
          alert(`Cada carta de interés debe tener al menos una imagen (Error en carta #${i + 1}).`);
          return;
        }
      }

      formDisable.value = true;

      // arma el JSON sin imágenes
      const data = {
        fecha: new Date().toJSON(),
        descripcion: form.descripcion,
        cartaOfrecida: {
          nombre: form.nombreCarta,
          juego: form.nombreJuego,
          estado: form.estado,
          imagenes: [],
        },
        precio: parseFloat(form.precio) > 0 ? parseFloat(form.precio) : null,
        cartasInteres: form.cartasDeInteres.map((carta) => ({
          nombre: carta.nombre,
          juego: carta.juego,
          estado: carta.estado,
          imagenes: [],
        })),
        publicador: {
          id: usuario.value.id,
          user: usuario.value.user,
        },
        estado: "ACTIVA",
      };

      const formData = new FormData();
      formData.append(
        "publicacion",
        new Blob([JSON.stringify(data)], { type: "application/json" })
      );

      form.imagenesCarta.forEach((file) => {
        formData.append("publicacionImagenes", file);
      });

      form.cartasDeInteres.forEach((carta, index) => {
        carta.imagenes.forEach((file) => {
          formData.append(`cartaInteres[${index}]`, file);
        });
      });

      fetch(backendURL + "/publicaciones", {
        method: "POST",
        body: formData,
        headers: {
          Authorization: "Bearer " + obtenerDatoCrudo("jwt"),
        },
      }).then((response) => response.json())
        .then((json) => {
          console.log(json);
          formDisable.value = false;
          window.location.href = "../";
        })
        .catch((err) => {
          formDisable.value = false;
          console.error(err);
          alert("Error al subir la publicación: " + err);
        });
      //setTimeout(()=>{formDisable.value = false}, 3000)
    };

    return {
      form,
      formDisable,
      sesionAbierta,
      jsonResultado,
      onMainImagesChange,
      onInterestImagesChange,
      agregarCartaInteres,
      eliminarCartaInteres,
      eliminarTodasCartasInteres,
      submitForm,
      resetImages,
      resetImagesOfertas,
      formatearTextoDeInput
    };
  },
}).mount("#app");
