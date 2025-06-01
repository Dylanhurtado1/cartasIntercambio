// Ahora mismo tengo slides dentro de distintos componentes de Vue, lo hago vanilla porque
// no tengo ganas de rediseñar todos para que acepten un componente adentro. Más fácil ponerlo
// full vanillas e insertarlo dentro del html. No estoy nada orgullo de esta solución, más teniendo Vue 
// Debería haber usado Vue CLI desde el principio lpm

export function ejecutarSliderVanilla() {
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

