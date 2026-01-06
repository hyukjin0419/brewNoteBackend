import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getOwner, updateMember } from '../lib/api';
import type { OwnerDetailResponse, MemberUpdateRequest } from '../types/member';
import { isAdmin } from '../utils/auth';
import './OwnerDetail.css';

function OwnerDetail() {
  const { ownerId } = useParams<{ ownerId: string }>();
  const navigate = useNavigate();
  const [owner, setOwner] = useState<OwnerDetailResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isEditMode, setIsEditMode] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [editForm, setEditForm] = useState<MemberUpdateRequest>({
    email: '',
    name: '',
    phoneNumber: '',
    status: '',
  });

  useEffect(() => {
    if (!ownerId) return;

    const fetchOwner = async () => {
      try {
        setIsLoading(true);
        setError(null);
        const data = await getOwner(ownerId);
        setOwner(data);
        // 수정 폼 초기화
        setEditForm({
          email: data.email || '',
          name: data.name || '',
          phoneNumber: data.phoneNumber || '',
          status: data.status || '',
        });
      } catch (err: any) {
        console.error('점주 조회 오류:', err);
        setError('점주 정보를 불러올 수 없습니다.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchOwner();
  }, [ownerId]);

  const handleEditClick = () => {
    setIsEditMode(true);
  };

  const handleCancelEdit = () => {
    if (owner) {
      setEditForm({
        email: owner.email || '',
        name: owner.name || '',
        phoneNumber: owner.phoneNumber || '',
        status: owner.status || '',
      });
    }
    setIsEditMode(false);
  };

  const handleInputChange = (field: keyof MemberUpdateRequest, value: string) => {
    setEditForm((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const handleUpdateSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!ownerId) return;

    try {
      setIsSubmitting(true);

      // 빈 값은 undefined로 변환하여 전송하지 않음
      const requestData: MemberUpdateRequest = {
        email: editForm.email?.trim() || undefined,
        name: editForm.name?.trim() || undefined,
        phoneNumber: editForm.phoneNumber?.trim() || undefined,
        status: editForm.status?.trim() || undefined,
      };

      await updateMember(ownerId, requestData);
      alert('점주 정보가 성공적으로 수정되었습니다.');

      // 데이터 다시 불러오기
      const updatedData = await getOwner(ownerId);
      setOwner(updatedData);
      setIsEditMode(false);
    } catch (error: any) {
      console.error('점주 수정 오류:', error);
      const errorMessage = error?.response?.data?.message ||
                          error?.response?.data?.error ||
                          error?.message ||
                          '점주 정보 수정에 실패했습니다.';
      alert(`점주 정보 수정에 실패했습니다.\n${errorMessage}`);
    } finally {
      setIsSubmitting(false);
    }
  };

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
          <div className="header-title-section">
            <h1>점주 상세 정보</h1>
            {isAdmin() && !isEditMode && (
              <button className="edit-button" onClick={handleEditClick}>
                수정하기
              </button>
            )}
          </div>
        </div>

        {isEditMode ? (
          <form onSubmit={handleUpdateSubmit} className="owner-edit-form">
            <div className="owner-info-section">
              <h2>기본 정보</h2>
              <div className="info-grid">
                <div className="info-item">
                  <label htmlFor="edit-email">이메일</label>
                  <input
                    id="edit-email"
                    type="email"
                    value={editForm.email}
                    onChange={(e) => handleInputChange('email', e.target.value)}
                    className="edit-input"
                    disabled={isSubmitting}
                  />
                </div>
                <div className="info-item">
                  <label htmlFor="edit-name">이름</label>
                  <input
                    id="edit-name"
                    type="text"
                    value={editForm.name}
                    onChange={(e) => handleInputChange('name', e.target.value)}
                    className="edit-input"
                    disabled={isSubmitting}
                  />
                </div>
                <div className="info-item">
                  <label htmlFor="edit-phone">전화번호</label>
                  <input
                    id="edit-phone"
                    type="tel"
                    value={editForm.phoneNumber}
                    onChange={(e) => handleInputChange('phoneNumber', e.target.value)}
                    className="edit-input"
                    disabled={isSubmitting}
                  />
                </div>
                <div className="info-item">
                  <label htmlFor="edit-status">상태</label>
                  <select
                    id="edit-status"
                    value={editForm.status}
                    onChange={(e) => handleInputChange('status', e.target.value)}
                    className="edit-select"
                    disabled={isSubmitting}
                  >
                    <option value="ACTIVE">ACTIVE</option>
                    <option value="INACTIVE">INACTIVE</option>
                  </select>
                </div>
              </div>
            </div>

            <div className="form-actions">
              <button
                type="button"
                className="cancel-button"
                onClick={handleCancelEdit}
                disabled={isSubmitting}
              >
                취소
              </button>
              <button
                type="submit"
                className="submit-button"
                disabled={isSubmitting}
              >
                {isSubmitting ? '저장 중...' : '저장하기'}
              </button>
            </div>
          </form>
        ) : (
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
        )}

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

