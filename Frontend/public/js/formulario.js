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
            this.form.imagenesCarta = Array.from(event.target.files);
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
        submitForm() {
            // VALIDACIONES
            if (this.form.imagenesCarta.length === 0) {
                alert('Debes subir al menos una imagen de la carta en venta.');
                return;
            }

            if ((parseFloat(this.form.precio) <= 0 || isNaN(parseFloat(this.form.precio))) && this.form.cartasDeInteres.length === 0) {
                alert('Debes ingresar un precio válido o al menos una carta de interés.');
                return;
            }

            for (let i = 0; i < this.form.cartasDeInteres.length; i++) {
                if (this.form.cartasDeInteres[i].imagenes.length === 0) {
                    alert(`Cada carta de interés debe tener al menos una imagen (Error en carta #${i + 1}).`);
                    return;
                }
            }

            // CREAR JSON
            const data = {
                fecha: new Date().toJSON(),
                descripcion: this.form.descripcion,
                cartaOfrecida: {
                    nombre: this.form.nombreCarta,
                    juego: this.form.nombreJuego,
                    estado: this.form.estado,
                    imagenes: this.form.imagenesCarta.map(file => file.name)        
                },
                precio: parseFloat(this.form.precio) > 0 ? parseFloat(this.form.precio) : null,
                cartasInteres: this.form.cartasDeInteres.map(
                    cartaInd => ({
                        nombre: cartaInd.nombre,
                        juego: cartaInd.juego,
                        estado: cartaInd.estado,
                        imagenes: cartaInd.imagenes.map(file => file.name)
                    }))
                ,
                publicador: {
                    id: null,
                    user: null
                } 
            };

            console.log(data)
            
            // CREAR PUBLICACIÓN EN SERVER
            fetch(backendURL + "/publicaciones", {
                method: "POST",
                body: JSON.stringify(data),
                headers: {
                    "Content-type": "application/json; charset=UTF-8",
                    "Authorization": "Bearer " + obtenerDatoCrudo("jwt")
                }
            })
            .then(response => response.json()) 
            .then(json => {
                console.log(json)
                window.location.href = '../'
            })
            .catch(err => {
                console.log(err)
                alert('Error con el servidor: ' + err)
            })

            this.jsonResultado = JSON.stringify(data, null, 2);
        }
    }
}).mount('#app');