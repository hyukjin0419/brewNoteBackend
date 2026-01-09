import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getStaff } from '../lib/api';
import type { StaffDetailResponse } from '../types/member';
import { CafeMemberStatus, CafeMemberRoleType } from '../types/member';
import './StaffDetail.css';

function StaffDetail() {
  const { cafeId, staffId } = useParams<{ cafeId: string; staffId: string }>();
  const navigate = useNavigate();
  const [staff, setStaff] = useState<StaffDetailResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!cafeId || !staffId) return;

    const fetchStaff = async () => {
      try {
        setIsLoading(true);
        setError(null);
        const data = await getStaff(cafeId, staffId);
        setStaff(data);
      } catch (err: any) {
        console.error('스태프 조회 오류:', err);
        const errorMessage = err?.response?.data?.message ||
                            err?.response?.data?.error ||
                            err?.message ||
                            '스태프 정보를 불러올 수 없습니다.';
        setError(errorMessage);
      } finally {
        setIsLoading(false);
      }
    };

    fetchStaff();
  }, [cafeId, staffId]);

  const getStatusBadgeClass = (status: CafeMemberStatus) => {
    switch (status) {
      case CafeMemberStatus.ACTIVATED:
        return 'status-activated';
      case CafeMemberStatus.PENDING:
        return 'status-pending';
      case CafeMemberStatus.LEAVE:
        return 'status-leave';
      default:
        return '';
    }
  };

  const getStatusLabel = (status: CafeMemberStatus) => {
    switch (status) {
      case CafeMemberStatus.ACTIVATED:
        return '활성';
      case CafeMemberStatus.PENDING:
        return '대기';
      case CafeMemberStatus.LEAVE:
        return '탈퇴';
      default:
        return status;
    }
  };

  const getRoleLabel = (role: CafeMemberRoleType) => {
    switch (role) {
      case CafeMemberRoleType.OWNER:
        return '점주';
      case CafeMemberRoleType.MANAGER:
        return '매니저';
      case CafeMemberRoleType.STAFF:
        return '스태프';
      default:
        return role;
    }
  };

  if (isLoading) {
    return (
      <div className="staff-detail-page">
        <div className="loading">로딩 중...</div>
      </div>
    );
  }

  if (error || !staff) {
    return (
      <div className="staff-detail-page">
        <div className="error">{error || '스태프를 찾을 수 없습니다.'}</div>
        <button className="back-button" onClick={() => navigate('/staffs')}>
          돌아가기
        </button>
      </div>
    );
  }

  return (
    <div className="staff-detail-page">
      <div className="detail-container">
        <div className="detail-header">
          <button className="back-button" onClick={() => navigate('/staffs')}>
            ← 스태프 관리로 돌아가기
          </button>
          <h1>스태프 상세 정보</h1>
        </div>

        <div className="staff-info-section">
          <h2>기본 정보</h2>
          <div className="info-grid">
            <div className="info-item">
              <label>이름</label>
              <div className="info-value">{staff.name}</div>
            </div>
            <div className="info-item">
              <label>닉네임</label>
              <div className="info-value">{staff.nickName || '-'}</div>
            </div>
            <div className="info-item">
              <label>이메일</label>
              <div className="info-value">{staff.email}</div>
            </div>
            <div className="info-item">
              <label>전화번호</label>
              <div className="info-value">{staff.phoneNumber || '-'}</div>
            </div>
            <div className="info-item">
              <label>역할</label>
              <div className="info-value">{getRoleLabel(staff.role)}</div>
            </div>
            <div className="info-item">
              <label>상태</label>
              <div className="info-value">
                <span className={`status-badge ${getStatusBadgeClass(staff.status)}`}>
                  {getStatusLabel(staff.status)}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default StaffDetail;

