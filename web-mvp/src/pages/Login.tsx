import { useState } from 'react';
import { login, getOwnersCafes, getStaffCafes } from '../lib/api';
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
      }
      
      // 점주/스태프인 경우 카페 목록 조회
      // 먼저 점주 API 호출, 실패하면 스태프 API 호출
      if (response.role === 'USER' || response.role === 'STAFF') {
        try {
          // 먼저 점주 카페 목록 조회 시도
          const ownersCafesData = await getOwnersCafes();
          if (ownersCafesData && ownersCafesData.cafes && Array.isArray(ownersCafesData.cafes) && ownersCafesData.cafes.length > 0) {
            // 점주인 경우
            setCafes(JSON.stringify(ownersCafesData.cafes));
            if (ownersCafesData.cafes.length > 0) {
              setSelectedCafeId(ownersCafesData.cafes[0].cafeId);
            }
          } else {
            // 점주 API가 null이거나 빈 배열이면 스태프 API 호출
            try {
              const staffCafesData = await getStaffCafes();
              if (staffCafesData && staffCafesData.cafes && Array.isArray(staffCafesData.cafes)) {
                // 스태프인 경우
                setCafes(JSON.stringify(staffCafesData.cafes));
                if (staffCafesData.cafes.length > 0) {
                  setSelectedCafeId(staffCafesData.cafes[0].cafeId);
                }
              }
            } catch (staffError: any) {
              console.error('스태프 카페 목록 조회 오류:', staffError);
              // 카페 목록 조회 실패해도 로그인은 진행
            }
          }
        } catch (ownerError: any) {
          // 점주 API 실패 시 스태프 API 호출
          console.error('점주 카페 목록 조회 오류:', ownerError);
          try {
            const staffCafesData = await getStaffCafes();
            if (staffCafesData && staffCafesData.cafes && Array.isArray(staffCafesData.cafes)) {
              // 스태프인 경우
              setCafes(JSON.stringify(staffCafesData.cafes));
              if (staffCafesData.cafes.length > 0) {
                setSelectedCafeId(staffCafesData.cafes[0].cafeId);
              }
            }
          } catch (staffError: any) {
            console.error('스태프 카페 목록 조회 오류:', staffError);
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
