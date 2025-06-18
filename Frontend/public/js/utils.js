// Bellísimo, lo sé
// USUARIO

function guardarDatoCrudo(key, value){localStorage.setItem(key, value);}

function guardarDatoObjeto(key, value){localStorage.setItem(key, JSON.stringify(value))};

function obtenerDatoCrudo(key){return localStorage.getItem(key);}

function obtenerDatoObjeto(key){return JSON.parse(localStorage.getItem(key))}

function vaciarDatos(){localStorage.clear()}

function sesionAbierta(){
    return (obtenerDatoCrudo("jwt") && obtenerDatoCrudo("usuarioActual")) !== null
}

// INTERCEPTADOR DE ERROR DE IMAGEN

function manejarErrorImagen(event) {
    const img = event.target;

    if (!img.dataset.fallback) {
    img.src = '/public/img/noCard.png';
    img.dataset.fallback = 'true';
    }
}

// URL DINÁMICO PORQUE EN AWS GENERA EL ID QUE SE LE CANTA

function obtenerURLDinamico(){
    /*
    const backendPort = 8080;
    const backendUrl = `${window.location.protocol}//${window.location.hostname}:${backendPort}`;
    console.log(backendUrl)
    return backendUrl
    */
    const hostname = window.location.hostname;
    const protocol = window.location.protocol;

    if(hostname == `localhost`) {
        return `${protocol}//${hostname}:8080/api`;
    }

    return `${protocol}//${hostname}/api`;
}

// Poderoso vanilla slider, sigo sin estar orgulloso de esta solución

function ejecutarSliderVanilla() {
    const slider = document.querySelectorAll("[data-slider]");
    console.log("Cantidad de sliders detectados: " + slider.length)
    slider.forEach((container) => {
        
        const slider = container.querySelector(".slider");
        const slides = slider.querySelectorAll(".slide");
        const prevBtn = container.querySelector(".prev");
        const nextBtn = container.querySelector(".next");
        let index = 0;

        const updateSlider = () => {
            slider.style.transform = `translateX(-${index * 100}%)`;
        };

        prevBtn.addEventListener("click", () => {
            index = (index - 1 + slides.length) % slides.length;
            updateSlider();
        });

        nextBtn.addEventListener("click", () => {
            index = (index + 1) % slides.length;
            updateSlider();
        });
    });
}



// Mis exports <3

export {guardarDatoCrudo, guardarDatoObjeto, obtenerDatoCrudo, 
    obtenerDatoObjeto, vaciarDatos, sesionAbierta, 
    manejarErrorImagen, obtenerURLDinamico as obtenerURL, ejecutarSliderVanilla};