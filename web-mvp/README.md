# brewNote Web MVP

레시피 조회 및 관리용 React 웹 애플리케이션

## 기술 스택

- React 18
- TypeScript
- Vite
- React Router
- Axios

## 시작하기

### 의존성 설치

```bash
cd web-mvp
npm install
```

### 개발 서버 실행

```bash
npm run dev
```

브라우저에서 `http://localhost:3000` 접속

### 빌드

```bash
npm run build
```

## 주요 기능

1. **레시피 검색**
   - 실시간 검색 (300ms 디바운스)
   - 검색 결과 리스트 표시
   - 레시피 상세 페이지 이동

2. **레시피 추가**
   - 프랜차이즈, 카테고리 선택
   - 옵션별 제조 단계 입력
   - 기본 옵션 설정

3. **레시피 상세**
   - 옵션별 제조 방법 표시
   - 기본 옵션 자동 선택
   - 옵션 변경 시 단계만 업데이트 (API 재호출 없음)

## API 엔드포인트

- `GET /api/recipe/search/recipes?keyword={keyword}` - 레시피 검색
- `GET /api/recipe/{recipeId}` - 레시피 상세 조회
- `GET /api/recipe/admin/recipes/form-data` - 레시피 폼 데이터
- `POST /api/recipe/admin/recipe` - 레시피 생성

## 프로젝트 구조

```
web-mvp/
├── src/
│   ├── pages/          # 페이지 컴포넌트
│   ├── lib/            # API 클라이언트
│   ├── types/          # TypeScript 타입 정의
│   ├── App.tsx         # 메인 앱 컴포넌트
│   └── main.tsx        # 진입점
├── package.json
└── vite.config.ts      # Vite 설정 (프록시 포함)
```


