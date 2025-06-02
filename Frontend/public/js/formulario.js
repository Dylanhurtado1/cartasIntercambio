const { createApp } = Vue;
import {obtenerDatoCrudo, sesionAbierta, obtenerDatoObjeto, obtenerURL} from './utils.js'

const backendURL = obtenerURL() 

const usuarioActual = obtenerDatoObjeto("usuarioActual");

createApp({
    data() {
        return {
            sesionAbierta: sesionAbierta(),
            form: {
                nombreCarta: '',
                nombreJuego: '',
                descripcion: '',
                estado: '',
                precio: '',
                imagenesCarta: [],
                cartasDeInteres: []
            },
            jsonResultado: '',
            formDisable: false
        };
    },
    mounted(){
        console.log("FUNCA")
        /*if(!sesionAbierta())
            window.location.href = "/"*/
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
        },
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
            this.formDisable = true
            // CREAR JSON (con las imágenes vacías)
            const data = {
                fecha: new Date().toJSON(),
                descripcion: this.form.descripcion,
                cartaOfrecida: {
                    nombre: this.form.nombreCarta,
                    juego: this.form.nombreJuego,
                    estado: this.form.estado,
                    imagenes: []
                },
                precio: parseFloat(this.form.precio) > 0 ? parseFloat(this.form.precio) : null,
                cartasInteres: this.form.cartasDeInteres.map(
                    cartaInd => ({
                        nombre: cartaInd.nombre,
                        juego: cartaInd.juego,
                        estado: cartaInd.estado,
                        imagenes: []
                    })),
                publicador: {
                    id: usuarioActual ? usuarioActual.id : null,
                    user: usuarioActual ? usuarioActual.user : null
                },
                estado: "ACTIVA"
            };

            // ---- ARMADO DEL MULTIPART ----
            const formData = new FormData();

            // JSON principal como Blob
            formData.append("publicacion", new Blob([JSON.stringify(data)], { type: "application/json" }));

            // Imagenes de la carta ofertada
            this.form.imagenesCarta.forEach(file => { 
                formData.append("publicacionImagenes", file); 
            });  

            // Imagenes por cada carta de interés
            this.form.cartasDeInteres.forEach((cartaInd, index) => {
                cartaInd.imagenes.forEach(file => {
                    formData.append(`cartaInteres[${index}]`, file);  
                }); 
            });

            
            /*
                La lógica que pensé para rehacer las imágenes de las cartas de interés es la siguiente:
                Lo que voy a guardar en cartaInteres[index] son las imagenes de las cartas de interés,
                de cada una, y el index es la posición de la lista de las cartas de interés de la publicación

                Un ejemplo idiota:
                Imaginate que tengo dos (2, como abogado escribo) cartas de interés, la primera tiene 3 cartas 
                y la segunda tiene 2 cartas
                
                Lo que se guardaría en el formdata es lo siguiente:
                    cartaInteres[0] = imagen1.png
                    cartaInteres[0] = imagen2.png
                    cartaInteres[0] = imagen3.png

                    cartaInteres[1] = imagen4.jpg
                    cartaInteres[1] = imagen5.jpg
                
                Lo que debería hacer back es recibir esto e iterar para guardarlas. Por cada imagen que tenga
                una carta de interés, hay que guardar el link que te devuelva la api/la cosa que hagan que guarde
                imágenes. En este ejemplo, hay que iterar las imagenes de cartaInteres[0] y guardar sus respectivos 
                links a la primera carta de interés, y lo mismo para la segunda carta.

                Si es su pregunta, si, podés hacer esto. Según vi, lo que tenés que cambiar en back es que reciba por parámetro
                
                @RequestPart("cartaInteres") Map<String, MultipartFile[]> imagenesDeCartasDeInteres
                             ^^^^^^^^^^^^^^ -> Re importante que tenga el mismo nombre
                
                Boludéz a aclarar, estoy guardando toda la data en el formdata. Mi idea principal es rehacer los datos
                que reciba el POST /publicaciones. En vez de enviarle un json, debería recibir el formdata y descomponerlo
                en los datos que envío. 

                Por lo que vi, sería de esta forma el back:

                @PostMapping("/publicaciones") 
                public ResponseEntity<String> uploadPublication(         
                    @RequestPart("publicacion") PublicationDto publicacion,         
                    @RequestPart("publicacionImagenes") MultipartFile[] cartaOfertadaImagenes,         
                    @RequestPart Map<String, MultipartFile[]> cartasInteresImagenes)
                {

                    // Creo que hay que hacer algo así con las imagenes de las cartas de interes. Todo lo siguiente 
                    // es pseudocódigo eh, no funcoina en java, es para mostrarlo por arriba como lo pensé el back 
                    
                    // itero por cara carta de interés que tenga
                    for (int i = 0; i < cantidadDeCartasDeInteres; i++) {

                        // obtengo la lista de las imagenes de "cartaInteres[0]", por ejemplo
                        MultipartFile[] CartaInteresImages = cartasInteresImagenes.getAll("cartaInteres[" + i + "]"); 
                        
                        //porque cada carta de interés debe tener mínimo una imagen, igual en front lo valido esto
                        if(CartaInteresImages.isNullOrEmpty())  
                            return error

                        // acá obtengo la carta de interés del momento
                        CartaDTO cartaDeInteresActual = publicacion.getCartaInteres(i)    

                        //itero por cada imagen para así guardarla en su respectiva carta
                        CartaInteresImages.forEach(imagen -> {
                            // por cada imagen asociada a la carta de interés, la guarda en la lista y listo
                            string url = apiSalvadoraQueGuardaImagen(imagen)
                            cartaDeInteresActual.guardarLink(url)
                        })       
                    }

                    // luego de esto, directo se guarda la publicación entera con los links actualizados
                }

                Obviamente asumo muy fuerte que van a armar "apiSalvadoraQueGuardaImagen", si no estamos en el horno JAJAJAJ

                "Mucho texto y poco código, por qué no lo hacés vos?" POR PURA PAJA, me divierte escribir mis ideas pero
                no programar back y front a la vez XDDDDDDDDDDDD 

                Novedad: al final lo hice yo hdps 
            */

            // --- POST multipart ---
            async function sleep(ms) {
                console.log("ENTRO SLEEP")
                return new Promise(resolve => setTimeout(resolve, ms));
            }

            fetch(backendURL + "/publicaciones", {
                method: "POST",
                body: formData,
                headers: {
                    "Authorization": "Bearer " + obtenerDatoCrudo("jwt")
                }
            })
            .then(response => response.json())
            .then(async (json) => {
                await sleep(10000)
                console.log(json);
                this.formDisable = false
                //window.location.href = '../';
            })
            .catch(err => {
                this.formDisable = false
                console.log(err);
                alert('Error con el servidor: ' + err);
            });

            //this.jsonResultado = JSON.stringify(data, null, 2);
        }
    }
}).mount('#app');