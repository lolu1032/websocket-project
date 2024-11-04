async function fetchWithAuth(url, options = {}) {
    try {
        const response = await fetch(url, {
            ...options,
            credentials: 'include'
        });

        // 401 상태인 경우 토큰 삭제 및 로그인 페이지로 리다이렉트
        if (response.status === 401) {
            handle401();
            window.location.href = '/login'; // 로그인 페이지로 리다이렉트
        }

        return response;
    } catch (error) {
        console.error('Fetch error:', error);
        throw error;
    }
}

function handle401() {
    document.cookie = "accessToken=; Max-Age=0; path=/;";
    document.cookie = "refreshToken=; Max-Age=0; path=/;";
}

document.addEventListener('DOMContentLoaded', () => {
    const logoutBtn = document.getElementById('logoutBtn');

    if (logoutBtn) {
        logoutBtn.addEventListener('click', async function(e) {
            e.preventDefault(); // 링크 기본 동작 방지

            try {
                // fetchWithAuth를 사용하여 로그아웃 요청 보내기
                const response = await fetchWithAuth('/auth/logout', {
                    method: 'POST', // 로그아웃 요청 방식
                });

                if (response.ok) {
                    // 로그아웃 성공 시 처리할 로직
                    alert('로그아웃 되었습니다.');
                    // 필요한 경우 페이지 리다이렉션
                    window.location.href = '/'; // 메인 페이지로 리다이렉션
                } else {
                    // 로그아웃 실패 시 처리할 로직
                    alert('로그아웃 실패: ' + response.statusText);
                }
            } catch (error) {
                alert('로그아웃 도중 오류가 발생했습니다: ' + error);
            }
        });
    }
});