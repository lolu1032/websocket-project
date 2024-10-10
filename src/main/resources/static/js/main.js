document.addEventListener('DOMContentLoaded', function () {
    const swiperSlides = document.querySelectorAll('.swiper');

    swiperSlides.forEach(function (element, index) {
        element.classList.add("swiper-" + index);
        let swiper = new Swiper(".swiper-" + index, {
            autoplay: {
                delay: 1,
                disableOnInteraction: false,
            },
            speed: 8000, // 8e3 대신 8000으로 명시적으로 변경
            loop: true,
            slidesPerView: "auto",
            freemode: true
        });
    });
});