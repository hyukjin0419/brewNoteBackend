import { useState } from 'react';
import { login, getOwnersCafes } from '../lib/api';
import { setToken, setRole, setCafes, setSelectedCafeId } from '../utils/auth';
import './Login.css';

function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!email || !password) {
      setError('이메일과 비밀번호를 입력해주세요.');
      return;
    }

    try {
      setIsLoading(true);
      const response = await login({ email, password });
      
      if (!response || !response.accessToken) {
        setError('로그인 응답에 토큰이 없습니다.');
        return;
      }
      
      setToken(response.accessToken);
      if (response.role) {
        setRole(response.role);
        
        // 점주(USER)인 경우 카페 목록 조회
        if (response.role === 'USER') {
          try {
            const cafesData = await getOwnersCafes();
            // 카페 전체 정보를 JSON 문자열로 저장
            setCafes(JSON.stringify(cafesData.ownedCafes));
            // 첫 번째 카페를 기본 선택
            if (cafesData.ownedCafes.length > 0) {
              setSelectedCafeId(cafesData.ownedCafes[0].cafeId);
            }
          } catch (cafeError: any) {
            console.error('카페 목록 조회 오류:', cafeError);
            // 카페 목록 조회 실패해도 로그인은 진행
          }
        }
      }
      // 페이지 새로고침하여 App의 인증 상태 업데이트
      window.location.href = '/';
    } catch (err: any) {
      console.error('로그인 오류 상세:', err);
      console.error('응답 데이터:', err?.response?.data);
      console.error('로그인 오류:', err);
      // 백엔드에서 보내는 에러 메시지 우선 사용
      const errorMessage = err?.response?.data?.message || 
                          '로그인에 실패했습니다.';
      setError(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-container">
        <h1>brewNote</h1>
        <h2>로그인</h2>
        <form onSubmit={handleSubmit} className="login-form">
          <div className="form-group">
            <label htmlFor="email">이메일</label>
            <input
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="이메일을 입력하세요"
              disabled={isLoading}
            />
          </div>
          <div className="form-group">
            <label htmlFor="password">비밀번호</label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="비밀번호를 입력하세요"
              disabled={isLoading}
            />
          </div>
          {error && <div className="error-message">{error}</div>}
          <button type="submit" disabled={isLoading} className="login-button">
            {isLoading ? '로그인 중...' : '로그인'}
          </button>
        </form>
      </div>
    </div>
  );
}

export default Login;
