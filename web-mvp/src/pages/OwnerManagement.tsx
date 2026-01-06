import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getOwners, getFranchises, createOwner } from '../lib/api';
import type { OwnerSummaryResponse, FranchiseResponse, CreateOwnerRequest } from '../types/member';
import './OwnerManagement.css';

function OwnerManagement() {
  const navigate = useNavigate();
  const [owners, setOwners] = useState<OwnerSummaryResponse[]>([]);
  const [franchises, setFranchises] = useState<FranchiseResponse[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isLoadingFranchises, setIsLoadingFranchises] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  
  const [createForm, setCreateForm] = useState<CreateOwnerRequest>({
    email: '',
    name: '',
    phoneNumber: '',
    franchiseId: '',
    cafeName: '',
    address: '',
  });
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    fetchOwners();
    fetchFranchises();
  }, [currentPage]);

  const fetchOwners = async () => {
    try {
      setIsLoading(true);
      setError(null);
      const response = await getOwners(currentPage, 20);
      setOwners(response.content);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
    } catch (err: any) {
      console.error('점주 목록 조회 오류:', err);
      setError('점주 목록을 불러올 수 없습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  const fetchFranchises = async () => {
    try {
      setIsLoadingFranchises(true);
      const data = await getFranchises();
      setFranchises(data);
    } catch (err: any) {
      console.error('프랜차이즈 조회 오류:', err);
    } finally {
      setIsLoadingFranchises(false);
    }
  };

  const handleCreateFormChange = (field: keyof CreateOwnerRequest, value: string) => {
    setCreateForm((prev) => ({ ...prev, [field]: value }));
  };

  const handleCreateSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!createForm.email || !createForm.name || !createForm.phoneNumber || 
        !createForm.franchiseId || !createForm.cafeName || !createForm.address) {
      setError('모든 필드를 입력해주세요.');
      return;
    }

    try {
      setIsSubmitting(true);
      await createOwner(createForm);
      alert('점주가 성공적으로 생성되었습니다.');
      setShowCreateForm(false);
      setCreateForm({
        email: '',
        name: '',
        phoneNumber: '',
        franchiseId: '',
        cafeName: '',
        address: '',
      });
      fetchOwners();
    } catch (err: any) {
      console.error('점주 생성 오류:', err);
      const errorMessage = err?.response?.data?.message || 
                          '점주 생성에 실패했습니다.';
      setError(errorMessage);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  return (
    <div className="owner-management-page">
      <div className="management-container">
        <button className="back-button" onClick={() => navigate('/')}>
          ← 검색으로 돌아가기
        </button>
        <div className="management-header">
          <h1>점주 관리</h1>
          <button 
            className="create-owner-button" 
            onClick={() => setShowCreateForm(!showCreateForm)}
          >
            {showCreateForm ? '생성 취소' : '점주 생성'}
          </button>
        </div>

        {showCreateForm && (
          <div className="create-form-section">
            <h2>점주 생성</h2>
            <form onSubmit={handleCreateSubmit} className="owner-create-form">
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
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="phoneNumber">전화번호 <span className="required">*</span></label>
                  <input
                    id="phoneNumber"
                    type="tel"
                    value={createForm.phoneNumber}
                    onChange={(e) => handleCreateFormChange('phoneNumber', e.target.value)}
                    placeholder="전화번호를 입력하세요"
                    disabled={isSubmitting}
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="franchiseId">프랜차이즈 <span className="required">*</span></label>
                  {isLoadingFranchises ? (
                    <div className="loading">로딩 중...</div>
                  ) : (
                    <select
                      id="franchiseId"
                      value={createForm.franchiseId}
                      onChange={(e) => handleCreateFormChange('franchiseId', e.target.value)}
                      disabled={isSubmitting}
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
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="cafeName">매장명 <span className="required">*</span></label>
                  <input
                    id="cafeName"
                    type="text"
                    value={createForm.cafeName}
                    onChange={(e) => handleCreateFormChange('cafeName', e.target.value)}
                    placeholder="매장명을 입력하세요"
                    disabled={isSubmitting}
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="address">주소 <span className="required">*</span></label>
                  <input
                    id="address"
                    type="text"
                    value={createForm.address}
                    onChange={(e) => handleCreateFormChange('address', e.target.value)}
                    placeholder="주소를 입력하세요"
                    disabled={isSubmitting}
                  />
                </div>
              </div>

              {error && <div className="error-message">{error}</div>}

              <div className="form-actions">
                <button type="submit" disabled={isSubmitting} className="submit-button">
                  {isSubmitting ? '생성 중...' : '점주 생성'}
                </button>
              </div>
            </form>
          </div>
        )}

        <div className="owners-list-section">
          <h2>점주 목록</h2>
          {isLoading ? (
            <div className="loading">로딩 중...</div>
          ) : error ? (
            <div className="error-message">{error}</div>
          ) : owners.length === 0 ? (
            <div className="empty-state">등록된 점주가 없습니다.</div>
          ) : (
            <>
              <div className="owners-table">
                <table>
                  <thead>
                    <tr>
                      <th>이메일</th>
                      <th>이름</th>
                      <th>전화번호</th>
                      <th>상태</th>
                      <th>대표 매장</th>
                    </tr>
                  </thead>
                  <tbody>
                    {owners.map((owner) => (
                      <tr 
                        key={owner.id}
                        className="owner-row"
                        onClick={() => navigate(`/owners/${owner.id}`)}
                      >
                        <td>{owner.email}</td>
                        <td>{owner.name}</td>
                        <td>{owner.phoneNumber}</td>
                        <td>
                          <span className={`status-badge status-${owner.status.toLowerCase()}`}>
                            {owner.status}
                          </span>
                        </td>
                        <td>{owner.representativeCafe || '-'}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {totalPages > 1 && (
                <div className="pagination">
                  <button
                    onClick={() => handlePageChange(currentPage - 1)}
                    disabled={currentPage === 0}
                    className="page-button"
                  >
                    이전
                  </button>
                  <span className="page-info">
                    {currentPage + 1} / {totalPages} (전체 {totalElements}명)
                  </span>
                  <button
                    onClick={() => handlePageChange(currentPage + 1)}
                    disabled={currentPage >= totalPages - 1}
                    className="page-button"
                  >
                    다음
                  </button>
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
}

export default OwnerManagement;

