import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getOwner } from '../lib/api';
import type { OwnerDetailResponse } from '../types/member';
import './OwnerDetail.css';

function OwnerDetail() {
  const { ownerId } = useParams<{ ownerId: string }>();
  const navigate = useNavigate();
  const [owner, setOwner] = useState<OwnerDetailResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!ownerId) return;

    const fetchOwner = async () => {
      try {
        setIsLoading(true);
        setError(null);
        const data = await getOwner(ownerId);
        setOwner(data);
      } catch (err: any) {
        console.error('점주 조회 오류:', err);
        setError('점주 정보를 불러올 수 없습니다.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchOwner();
  }, [ownerId]);

  if (isLoading) {
    return (
      <div className="owner-detail-page">
        <div className="loading">로딩 중...</div>
      </div>
    );
  }

  if (error || !owner) {
    return (
      <div className="owner-detail-page">
        <div className="error">{error || '점주를 찾을 수 없습니다.'}</div>
        <button className="back-button" onClick={() => navigate('/owners')}>
          돌아가기
        </button>
      </div>
    );
  }

  return (
    <div className="owner-detail-page">
      <div className="detail-container">
        <div className="detail-header">
          <button className="back-button" onClick={() => navigate('/owners')}>
            ← 점주 관리로 돌아가기
          </button>
          <h1>점주 상세 정보</h1>
        </div>

        <div className="owner-info-section">
          <h2>기본 정보</h2>
          <div className="info-grid">
            <div className="info-item">
              <label>이메일</label>
              <div className="info-value">{owner.email}</div>
            </div>
            <div className="info-item">
              <label>이름</label>
              <div className="info-value">{owner.name}</div>
            </div>
            <div className="info-item">
              <label>전화번호</label>
              <div className="info-value">{owner.phoneNumber}</div>
            </div>
            <div className="info-item">
              <label>상태</label>
              <div className="info-value">
                <span className={`status-badge status-${owner.status.toLowerCase()}`}>
                  {owner.status}
                </span>
              </div>
            </div>
          </div>
        </div>

        <div className="cafes-section">
          <h2>매장 목록</h2>
          {owner.cafeSummaries && owner.cafeSummaries.length > 0 ? (
            <div className="cafes-list">
              {owner.cafeSummaries.map((cafe) => (
                <div key={cafe.cafeId} className="cafe-item">
                  <div className="cafe-header">
                    <h3>{cafe.cafeName}</h3>
                    <span className={`status-badge status-${cafe.status.toLowerCase()}`}>
                      {cafe.status}
                    </span>
                  </div>
                  <div className="cafe-address">
                    <strong>주소:</strong> {cafe.cafeAddress}
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="empty-state">등록된 매장이 없습니다.</div>
          )}
        </div>
      </div>
    </div>
  );
}

export default OwnerDetail;

