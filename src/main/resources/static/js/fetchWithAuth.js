async function fetchWithAuth(url, options = {}) {
    try {
        const response = await fetch(url, {
            ...options,
            credentials: 'include'
        });

        // 401 상태인 경우 토큰 삭제 및 로그인 페이지로 리다이렉트
        if (response.status === 401) {
            handle401();
        }

        return response;
    } catch (error) {
        console.error('Fetch error:', error);
        throw error;
    }
}

function handle401() {
    // 쿠키에서 accessToken과 refreshToken 삭제
    document.cookie = "accessToken=; Max-Age=0; path=/;";
    document.cookie = "refreshToken=; Max-Age=0; path=/;";
}
