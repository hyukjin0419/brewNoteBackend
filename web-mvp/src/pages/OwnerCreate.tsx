import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { createOwner, getFranchises } from '../lib/api';
import type { FranchiseResponse } from '../types/member';
import './OwnerCreate.css';

function OwnerCreate() {
  const navigate = useNavigate();
  const [franchises, setFranchises] = useState<FranchiseResponse[]>([]);
  const [form, setForm] = useState({
    email: '',
    name: '',
    phoneNumber: '',
    franchiseId: '',
    cafeName: '',
    address: '',
  });
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isLoadingFranchises, setIsLoadingFranchises] = useState(true);

  useEffect(() => {
    const fetchFranchises = async () => {
      try {
        setIsLoadingFranchises(true);
        const data = await getFranchises();
        setFranchises(data);
      } catch (err: any) {
        console.error('프랜차이즈 조회 오류:', err);
        setError('프랜차이즈 목록을 불러올 수 없습니다.');
      } finally {
        setIsLoadingFranchises(false);
      }
    };
    fetchFranchises();
  }, []);

  const handleInputChange = (field: string, value: string) => {
    setForm((prev) => ({ ...prev, [field]: value }));
    setError(null);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!form.email || !form.name || !form.phoneNumber || !form.franchiseId || !form.cafeName || !form.address) {
      setError('모든 필드를 입력해주세요.');
      return;
    }

    try {
      setIsLoading(true);
      await createOwner(form);
      alert('점주가 성공적으로 생성되었습니다.');
      navigate('/');
    } catch (err: any) {
      console.error('점주 생성 오류:', err);
      const errorMessage = err?.response?.data?.message || 
                          '점주 생성에 실패했습니다.';
      setError(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="owner-create-page">
      <div className="create-container">
        <button className="back-button" onClick={() => navigate('/')}>
          ← 검색으로 돌아가기
        </button>
        <h1>점주 생성</h1>
        <form onSubmit={handleSubmit} className="owner-form">
          <div className="form-group">
            <label htmlFor="email">이메일 <span className="required">*</span></label>
            <input
              id="email"
              type="email"
              value={form.email}
              onChange={(e) => handleInputChange('email', e.target.value)}
              placeholder="이메일을 입력하세요"
              disabled={isLoading}
            />
          </div>

          <div className="form-group">
            <label htmlFor="name">이름 <span className="required">*</span></label>
            <input
              id="name"
              type="text"
              value={form.name}
              onChange={(e) => handleInputChange('name', e.target.value)}
              placeholder="이름을 입력하세요"
              disabled={isLoading}
            />
          </div>

          <div className="form-group">
            <label htmlFor="phoneNumber">전화번호 <span className="required">*</span></label>
            <input
              id="phoneNumber"
              type="tel"
              value={form.phoneNumber}
              onChange={(e) => handleInputChange('phoneNumber', e.target.value)}
              placeholder="전화번호를 입력하세요"
              disabled={isLoading}
            />
          </div>

          <div className="form-group">
            <label htmlFor="franchiseId">프랜차이즈 <span className="required">*</span></label>
            {isLoadingFranchises ? (
              <div className="loading">프랜차이즈 목록을 불러오는 중...</div>
            ) : (
              <select
                id="franchiseId"
                value={form.franchiseId}
                onChange={(e) => handleInputChange('franchiseId', e.target.value)}
                disabled={isLoading}
              >
                <option value="">프랜차이즈를 선택하세요</option>
                {franchises.map((franchise) => (
                  <option key={franchise.franchiseId} value={franchise.franchiseId}>
                    {franchise.name}
                  </option>
                ))}
              </select>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="cafeName">매장명 <span className="required">*</span></label>
            <input
              id="cafeName"
              type="text"
              value={form.cafeName}
              onChange={(e) => handleInputChange('cafeName', e.target.value)}
              placeholder="매장명을 입력하세요"
              disabled={isLoading}
            />
          </div>

          <div className="form-group">
            <label htmlFor="address">주소 <span className="required">*</span></label>
            <input
              id="address"
              type="text"
              value={form.address}
              onChange={(e) => handleInputChange('address', e.target.value)}
              placeholder="주소를 입력하세요"
              disabled={isLoading}
            />
          </div>

          {error && <div className="error-message">{error}</div>}

          <div className="form-actions">
            <button type="button" onClick={() => navigate('/')} className="cancel-button">
              취소
            </button>
            <button type="submit" disabled={isLoading || isLoadingFranchises} className="submit-button">
              {isLoading ? '생성 중...' : '점주 생성'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default OwnerCreate;

