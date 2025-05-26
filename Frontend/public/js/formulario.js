const { createApp } = Vue;
import {obtenerDatoCrudo, sesionAbierta} from './datos.js'


const backendURL = "http://localhost:8080"; //ahora como modo de prueba, se dejará el link acá 

createApp({
    data() {
        return {
            form: {
                nombreCarta: '',
                nombreJuego: '',
                descripcion: '',
                estado: '',
                precio: '',
                imagenesCarta: [],
                cartasDeInteres: []
            },
            jsonResultado: ''
        };
    },
    mounted(){
        console.log("FUNCA")
        if(!sesionAbierta())
            window.location.href = "/"
    },
    methods: {
        onMainImagesChange(event) {
            this.form.imagenesCarta = event.target.files.length > 0 ? [event.target.files[0]] : [];
        },
        onInterestImagesChange(event, index) {
            this.form.cartasDeInteres[index].imagenes = Array.from(event.target.files);
        },
        agregarCartaInteres() {
            this.form.cartasDeInteres.push({
                nombre: '',
                juego: '',
                estado: '',
                imagenes: []
            });
        },
        eliminarCartaInteres(index) {
            this.form.cartasDeInteres.splice(index, 1);
        },
        eliminarTodasCartasInteres(){
            this.form.cartasDeInteres = []
        }
        ,
        async submitForm() {
            // Validaciones, igual que tenías...
        
            if (this.form.imagenesCarta.length === 0) {
                alert('Debes subir al menos una imagen de la carta en venta.');
                return;
            }
            // ...otras validaciones...
        
            // 1. Armar datos de publicación SIN images (no mandar file names)
            const data = {
                fecha: new Date().toJSON(),
                descripcion: this.form.descripcion,
                cartaOfrecida: {
                    nombre: this.form.nombreCarta,
                    juego: this.form.nombreJuego,
                    estado: this.form.estado,
                    // Omitir "imagenes"
                },
                precio: parseFloat(this.form.precio) > 0 ? parseFloat(this.form.precio) : null,
                cartasInteres: this.form.cartasDeInteres.map(
                    cartaInd => ({
                        nombre: cartaInd.nombre,
                        juego: cartaInd.juego,
                        estado: cartaInd.estado
                        // Omitir "imagenes"
                    })),
                publicador: {
                    id: null,
                    user: null
                }
            };
        
            try {
                // Paso 1: Crear publicación
                const res = await fetch(backendURL + "/publicaciones", {
                    method: "POST",
                    body: JSON.stringify(data),
                    headers: {
                        "Content-type": "application/json; charset=UTF-8",
                        "Authorization": "Bearer " + obtenerDatoCrudo("jwt")
                    }
                });
                const json = await res.json();
                const publicacionId = json.id;
        
                // Paso 2: Subir imagen principal
                if (this.form.imagenesCarta.length > 0) {
                    const formData = new FormData();
                    formData.append("imagen", this.form.imagenesCarta[0]);
        
                    const resImg = await fetch(backendURL + `/publicaciones/${publicacionId}/imagen`, {
                        method: "POST",
                        headers: {
                            "Authorization": "Bearer " + obtenerDatoCrudo("jwt")
                        },
                        body: formData
                    });
                    if (!resImg.ok) throw new Error("Error al subir imagen");
                }
                
                for (let idx = 0; idx < this.form.cartasDeInteres.length; idx++) {
                    const carta = this.form.cartasDeInteres[idx];
                    if (carta.imagenes && carta.imagenes.length > 0) {
                      const formData = new FormData();
                      formData.append("imagen", carta.imagenes[0]);
                      await fetch(`${backendURL}/publicaciones/${publicacionId}/cartasInteres/${idx}/imagen`, {
                        method: "POST",
                        headers: { "Authorization": "Bearer " + obtenerDatoCrudo("jwt") },
                        body: formData
                      });
                    }
                  }
              
        
                // Redirigir o actualizar vista
                window.location.href = '../';
        
            } catch (err) {
                console.log(err)
                alert('Error con el servidor: ' + err)
            }
        }
    }
}).mount('#app');