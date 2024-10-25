document.addEventListener('DOMContentLoaded', function () {
    const swiperSlides = document.querySelectorAll('.swiper');

    swiperSlides.forEach(function (element, index) {
        element.classList.add("swiper-" + index);
        let swiper = new Swiper(".swiper-" + index, {
            autoplay: {
                delay: 1,
                disableOnInteraction: false,
            },
            speed: 8000,
            loop: true,
            slidesPerView: "auto",
            freemode: true
        });
    });
});
$(document).ready(function() {
    $('#sel-op').change(function() {
        const selectedCategory = $(this).val();
        const currentPage = $('.pagination .active').data('page') || 0; // 현재 페이지 번호

        // AJAX POST 요청
        $.ajax({
            type: 'POST',
            url: '/posts',
            data: {
                category: selectedCategory,
                page: currentPage // 페이지 번호를 그대로 사용
            },
            success: function(response) {
                // 게시글 목록과 페이징 부분을 업데이트
                $('div.recruitment-sel').nextAll().remove(); // 기존 게시글 목록과 페이징 제거
                $(response).appendTo('div.recruitment-sel').nextAll(); // 새로운 게시글 목록과 페이징 추가
            },
            error: function() {
                alert('게시글 로드 중 오류가 발생했습니다.');
            }
        });
    });
});


