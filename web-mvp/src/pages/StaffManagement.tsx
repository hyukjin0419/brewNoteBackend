import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getOwnersCafes, getStaffs, createStaff } from '../lib/api';
import type { OwnedCafeSummary, StaffSummaryResponse, StaffCreateRequest } from '../types/member';
import { CafeMemberStatus } from '../types/member';
import { getCafes, getSelectedCafeId, setCafes as saveCafes, setSelectedCafeId as saveSelectedCafeId } from '../utils/auth';
import './StaffManagement.css';

function StaffManagement() {
  const navigate = useNavigate();
  const [cafes, setCafes] = useState<OwnedCafeSummary[]>([]);
  const [selectedCafeId, setSelectedCafeId] = useState<string>('');
  const [staffs, setStaffs] = useState<StaffSummaryResponse[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isLoadingStaffs, setIsLoadingStaffs] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const [createForm, setCreateForm] = useState<StaffCreateRequest>({
    cafeId: '',
    email: '',
    name: '',
    phoneNumber: '',
  });

  // 카페 목록 로드
  useEffect(() => {
    const fetchCafes = async () => {
      try {
        setIsLoading(true);
        setError(null);
        
        // 먼저 localStorage에서 카페 목록 확인
        const savedCafesJson = getCafes();
        if (savedCafesJson) {
          try {
            const savedCafes: OwnedCafeSummary[] = JSON.parse(savedCafesJson);
            if (savedCafes.length > 0) {
              setCafes(savedCafes);
              const savedCafeId = getSelectedCafeId() || savedCafes[0].cafeId;
              setSelectedCafeId(savedCafeId); // state 업데이트
              if (!getSelectedCafeId()) {
                saveSelectedCafeId(savedCafeId); // localStorage 저장
              }
              setCreateForm(prev => ({ ...prev, cafeId: savedCafeId }));
              setIsLoading(false);
              return; // 저장된 데이터 사용
            }
          } catch (parseError) {
            console.error('저장된 카페 목록 파싱 오류:', parseError);
          }
        }
        
        // 저장된 데이터가 없으면 API 호출
        const data = await getOwnersCafes();
        setCafes(data.ownedCafes); // state 업데이트
        
        // 카페 목록을 localStorage에 저장
        saveCafes(JSON.stringify(data.ownedCafes));
        
        // 첫 번째 카페를 기본 선택
        if (data.ownedCafes.length > 0) {
          const firstCafeId = data.ownedCafes[0].cafeId;
          setSelectedCafeId(firstCafeId); // state 업데이트
          saveSelectedCafeId(firstCafeId); // localStorage 저장
          setCreateForm(prev => ({ ...prev, cafeId: firstCafeId }));
        }
      } catch (err: any) {
        console.error('카페 목록 조회 오류:', err);
        console.error('에러 응답:', err?.response);
        console.error('에러 상태:', err?.response?.status);
        const errorMessage = err?.response?.data?.message ||
                            err?.response?.data?.error ||
                            err?.message ||
                            '카페 목록을 불러올 수 없습니다.';
        setError(errorMessage);
      } finally {
        setIsLoading(false);
      }
    };

    fetchCafes();
  }, []);

  // 선택한 카페의 스태프 목록 로드
  useEffect(() => {
    if (!selectedCafeId) return;

    const fetchStaffs = async () => {
      try {
        setIsLoadingStaffs(true);
        setError(null);
        const data = await getStaffs(selectedCafeId, currentPage, 20);
        setStaffs(data.content);
        setTotalPages(data.totalPages);
        setTotalElements(data.totalElements);
      } catch (err: any) {
        console.error('스태프 목록 조회 오류:', err);
        console.error('에러 응답:', err?.response);
        const errorMessage = err?.response?.data?.message ||
                            err?.response?.data?.error ||
                            err?.message ||
                            '스태프 목록을 불러올 수 없습니다.';
        setError(errorMessage);
      } finally {
        setIsLoadingStaffs(false);
      }
    };

    fetchStaffs();
  }, [selectedCafeId, currentPage]);

  const handleCafeChange = (cafeId: string) => {
    setSelectedCafeId(cafeId); // state 업데이트
    saveSelectedCafeId(cafeId); // localStorage 저장
    setCreateForm(prev => ({ ...prev, cafeId }));
    setCurrentPage(0); // 카페 변경 시 첫 페이지로
  };

  const handleCreateFormChange = (field: keyof StaffCreateRequest, value: string) => {
    setCreateForm(prev => ({
      ...prev,
      [field]: value,
    }));
  };

  const handleCreateSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!createForm.email || !createForm.name || !createForm.phoneNumber) {
      alert('모든 필드를 입력해주세요.');
      return;
    }

    if (!createForm.cafeId) {
      alert('카페를 선택해주세요.');
      return;
    }

    try {
      setIsSubmitting(true);
      await createStaff(createForm);
      alert('스태프가 성공적으로 생성되었습니다.');
      
      // 폼 초기화
      setCreateForm({
        cafeId: selectedCafeId,
        email: '',
        name: '',
        phoneNumber: '',
      });
      setShowCreateForm(false);
      
      // 스태프 목록 다시 불러오기
      const data = await getStaffs(selectedCafeId, currentPage, 20);
      setStaffs(data.content);
      setTotalPages(data.totalPages);
      setTotalElements(data.totalElements);
    } catch (err: any) {
      console.error('스태프 생성 오류:', err);
      const errorMessage = err?.response?.data?.message ||
                          err?.response?.data?.error ||
                          err?.message ||
                          '스태프 생성에 실패했습니다.';
      alert(`스태프 생성에 실패했습니다.\n${errorMessage}`);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handlePageChange = (newPage: number) => {
    if (newPage >= 0 && newPage < totalPages) {
      setCurrentPage(newPage);
    }
  };

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

  if (isLoading) {
    return (
      <div className="staff-management-page">
        <div className="loading">로딩 중...</div>
      </div>
    );
  }

  if (error && cafes.length === 0) {
    return (
      <div className="staff-management-page">
        <div className="error">{error}</div>
        <button className="back-button" onClick={() => navigate('/')}>
          돌아가기
        </button>
      </div>
    );
  }

  return (
    <div className="staff-management-page">
      <div className="management-container">
        <button className="back-button" onClick={() => navigate('/')}>
          ← 검색으로 돌아가기
        </button>
        <div className="management-header">
          <h1>스태프 관리</h1>
          <button
            className="create-staff-button"
            onClick={() => setShowCreateForm(!showCreateForm)}
            disabled={!selectedCafeId}
          >
            {showCreateForm ? '생성 취소' : '스태프 추가'}
          </button>
        </div>

        {cafes.length > 0 && (
          <div className="cafe-selector">
            <label htmlFor="cafe-select">매장 선택</label>
            <select
              id="cafe-select"
              value={selectedCafeId}
              onChange={(e) => handleCafeChange(e.target.value)}
              className="cafe-select"
            >
              {cafes.map((cafe) => (
                <option key={cafe.cafeId} value={cafe.cafeId}>
                  {cafe.CafeName}
                </option>
              ))}
            </select>
          </div>
        )}

        {showCreateForm && (
          <div className="create-form-section">
            <h2>스태프 생성</h2>
            <form onSubmit={handleCreateSubmit} className="staff-create-form">
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="email">이메일 <span className="required">*</span></label>
                  <input
                    id="email"
                    type="email"
                    value={createForm.email}
                    onChange={(e) => handleCreateFormChange('email', e.target.value)}
                    placeholder="이메일을 입력하세요"
                    disabled={isSubmitting}
                    required
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="name">이름 <span className="required">*</span></label>
                  <input
                    id="name"
                    type="text"
                    value={createForm.name}
                    onChange={(e) => handleCreateFormChange('name', e.target.value)}
                    placeholder="이름을 입력하세요"
                    disabled={isSubmitting}
                    required
                  />
                </div>
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="phone">전화번호 <span className="required">*</span></label>
                  <input
                    id="phone"
                    type="tel"
                    value={createForm.phoneNumber}
                    onChange={(e) => handleCreateFormChange('phoneNumber', e.target.value)}
                    placeholder="전화번호를 입력하세요"
                    disabled={isSubmitting}
                    required
                  />
                </div>
              </div>
              <div className="form-actions">
                <button
                  type="button"
                  className="cancel-button"
                  onClick={() => {
                    setShowCreateForm(false);
                    setCreateForm({
                      cafeId: selectedCafeId,
                      email: '',
                      name: '',
                      phoneNumber: '',
                    });
                  }}
                  disabled={isSubmitting}
                >
                  취소
                </button>
                <button
                  type="submit"
                  className="submit-button"
                  disabled={isSubmitting}
                >
                  {isSubmitting ? '생성 중...' : '생성하기'}
                </button>
              </div>
            </form>
          </div>
        )}

        {selectedCafeId && (
          <div className="staff-list-section">
            <h2>스태프 목록</h2>
            {isLoadingStaffs ? (
              <div className="loading">로딩 중...</div>
            ) : staffs.length === 0 ? (
              <div className="empty-state">등록된 스태프가 없습니다.</div>
            ) : (
              <>
                <div className="staff-table-container">
                  <table className="staff-table">
                    <thead>
                      <tr>
                        <th>이름</th>
                        <th>닉네임</th>
                        <th>이메일</th>
                        <th>전화번호</th>
                        <th>역할</th>
                        <th>상태</th>
                      </tr>
                    </thead>
                    <tbody>
                      {staffs.map((staff) => (
                        <tr 
                          key={staff.cafeMemberId}
                          className="staff-row"
                          onClick={() => navigate(`/staffs/${selectedCafeId}/${staff.memberId}`)}
                        >
                          <td>{staff.name}</td>
                          <td>{staff.nickName || '-'}</td>
                          <td>{staff.email}</td>
                          <td>{staff.phoneNumber || '-'}</td>
                          <td>{staff.role}</td>
                          <td>
                            <span className={`status-badge ${getStatusBadgeClass(staff.status)}`}>
                              {getStatusLabel(staff.status)}
                            </span>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>

                {totalPages > 1 && (
                  <div className="pagination">
                    <button
                      className="pagination-button"
                      onClick={() => handlePageChange(currentPage - 1)}
                      disabled={currentPage === 0}
                    >
                      이전
                    </button>
                    <span className="pagination-info">
                      {currentPage + 1} / {totalPages} (총 {totalElements}명)
                    </span>
                    <button
                      className="pagination-button"
                      onClick={() => handlePageChange(currentPage + 1)}
                      disabled={currentPage >= totalPages - 1}
                    >
                      다음
                    </button>
                  </div>
                )}
              </>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

export default StaffManagement;

