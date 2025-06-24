// Bellísimo, lo sé
// USUARIO

function guardarDatoCrudo(key, value){localStorage.setItem(key, value);}

function guardarDatoObjeto(key, value){localStorage.setItem(key, JSON.stringify(value))};

function obtenerDatoCrudo(key){return localStorage.getItem(key);}

function sesionAbierta(){
    return (getUserData() !== null)
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
    const hostname = window.location.hostname
    const protocol = window.location.protocol
    var URL = `${protocol}//${hostname}`
    console.log("URL: " + URL)

    if(hostname == "localhost") // si estamos en deploy, se agrega el puerto para ser felices
        URL += ":8080" // MMMMMMM, 100% seguro dejar esto acá

    URL += "/api";

    return URL
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

// Comienzo de cookie local carajo

function setUserData(value) {
    // Solo debería llamar a esta función cuando se loguea, es un "por las dudas"
    if (getUserData() !== null) {
        throw new Error("La cookie 'userData' ya existe.")
    }

    const expires = new Date()
    expires.setTime(expires.getTime() + (1 * 60 * 60 * 1000)) // vence lo mismo que el jwt
    const expiresString = "expires=" + expires.toUTCString()
    const valueString = encodeURIComponent(JSON.stringify(value)) // lo convierto en un json y lo limpio de boludeces con el "encodeURIComponent" 
    document.cookie = "userData=" + valueString + ";" + expiresString + ";path=/"
}

function getUserData() {
    const nameEq = "userData=";
    const cookies = document.cookie.split(';');
    
    for (let i = 0; i < cookies.length; i++) {
        let cookie = cookies[i];
        while (cookie.charAt(0) === ' ') {
            cookie = cookie.substring(1, cookie.length); // elimino espacios en blanco, la cookie debe ser "pura"
        }
        if (cookie.indexOf(nameEq) === 0) {
            const value = decodeURIComponent(cookie.substring(nameEq.length, cookie.length)); 
            return JSON.parse(value); // lo convierto a un objeto y lo devuelvo 
        }
    }
    return null
}

function deleteUserData() {
    document.cookie = "userData=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/"
    const url = obtenerURLDinamico()
    fetch(url + "usuarios/logout", { credentials: "include"})
}

// Mis exports <3

export {guardarDatoCrudo, guardarDatoObjeto, obtenerDatoCrudo, 
    sesionAbierta, // el único superviviente del borrado del localstorage, cuando esté la cookie por el back se MUERE 
    manejarErrorImagen, obtenerURLDinamico as obtenerURL, ejecutarSliderVanilla,
    setUserData, getUserData, deleteUserData};