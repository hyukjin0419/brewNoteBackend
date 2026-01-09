# ☕ Blendery – Backend Architecture

## 0. Project Overview
Blendery는 카페 매장에서 메뉴 레시피를 찾는 과정에서 발생하는 탐색 지연 문제를 줄이기 위해 설계된 레시피 관리 API 서버입니다.

단순히 데이터를 저장하는 것이 아니라, 실제 매장 환경에서 쓰일 수 있는 구조를 목표로 했습니다.

### 0.1 시현 및 Swagger
https://blendery5.netlify.app/
- 해당 웹페이지를 통하여 서버에서 산출한 api를 시험해보실 수 있습니다.
- 로그인을 위한 이메일과 비밀번호는 부스에 있는 서버(최혁진)을 찾아주세요.

https://blendery.store/swagger-ui/index.html  

## 1. 서버 설계에서 가장 먼저 고민한 문제들
단순한 CRUD 구현이 아니라, 카페 운영 환경에서 실제로 불편한 지점을 서버에서 어떻게 풀 수 있을지를 먼저 고민했습니다.

### 1.1 검색에 대하여
- 바쁜 매장 상황에서는 메뉴를 **빠르게 찾는 것**이 굉장히 중요하다고 판단했습니다.
- 메뉴 이름뿐 아니라 초성으로도 검색할 수 있도록 검색을 위한 별도의 필드를 함께 관리하고 있습니다.
- 또한 검색 결과에 점수를 부여해, 사용자가 의도했을 가능성이 높은 메뉴가 먼저 노출되도록 설계했습니다.

### 1.2 대외비인 레시피에 대하여
- 프랜차이즈의 음료 레시피는 일반적으로 외부에 공개되지 않는 대외비 정보라고 판단했습니다.
- 그래서 일반적인 회원가입 방식 대신, 상위 권한을 가진 계정이 하위 계정을 직접 생성하는 구조를 선택했습니다.
- 사용자는 자신이 속한 카페가 포함된 프랜차이즈의 레시피만 조회할 수 있도록 접근 범위를 제한했습니다.

### 1.3 다대다 대이터 구조 적극 활용
- 하나의 메뉴를 그대로 저장하면, 옵션이 늘어날수록 데이터 관리가 어려워진다고 판단했습니다.
- 그래서 메뉴 자체와 실제 제조 레시피를 분리하고, 옵션에 따라 레시피를 나누어 저장하도록 설계했습니다.
- 또한 한 사람이 여러 카페에서 일할 수 있는 환경을 고려해, 사용자와 카페의 관계를 별도의 구조로 관리하고 있습니다.


## 2. ERD 및 데이터 구조 설계
[![ERD](https://dbdiagram.io/d/691fce14228c5bbc1ad659f3)](https://dbdiagram.io/d/691fce14228c5bbc1ad659f3)
- 위 ERD는 레시피를 옵션 단위로 관리하고, 카페 소속에 따라 접근 권한이 달라지는 구조를 중심으로 설계되었습니다.
- 분기된 레시피(Variant)에 대해 카페에 소속된 사용자(CafeMember)만 즐겨찾기를 추가할 수 있도록 구성했습니다.


## 3. 핵심 API 흐름
### 3.1 검색
- 검색은 Blendery 서버에서 가장 중요하게 다룬 기능입니다. 매장 환경에서는 사용자가 정확한 메뉴명을 입력하지 않는 경우가 많고, 입력 자체도 짧게 이루어지는 편이 더 유리하다고 판단했습니다.  
  그래서 검색 흐름은 다음과 같은 순서로 처리됩니다.
  
- 먼저 레시피가 저장될 때, 레시피의 **이름(title)** 과 **별칭(alias)** 을 함께 관리하고, 각각에 대해 **초성 정보**를 미리 생성해 DB에 저장합니다.
  
- 사용자가 검색어를 입력하면, 서버는 해당 입력값을 그대로 사용하지 않고 검색에 적합한 형태로 한 번 더 가공합니다.
  입력값에서 초성 검색이 가능한지,완성형 문자열 검색이 가능한지를 먼저 판단합니다.
  
- 이후 검색은 한 번에 모든 레시피를 대상으로 수행하지 않고, 우선 조건에 맞는 후보 레시피를 제한된 개수로 가져옵니다. 이를 통해 불필요하게 많은 데이터를 비교하지 않도록 했습니다.
  
- 후보 레시피가 정해지면, 각 레시피에 대해 제목과 별칭을 기준으로 여러 방식의 매칭을 시도합니다.

- 메뉴 이름이 완전히 일치하는 경우, 부분 일치하는 경우, 초성이 일치하는 경우에 따라 서로 다른 점수를 부여합니다.

- 별칭(alias)이 매칭되는 경우에도 별도의 점수를 추가하였습니다.

- 이렇게 계산된 점수를 기준으로 레시피 후보를 정렬한 뒤, 상위 결과만 최종 검색 결과로 반환합니다.

- 이 과정에서 제목이나 별칭 중 어느 쪽에서도 매칭되지 않은 레시피는 검색 결과에서 제외됩니다.


정리하면, Blendery의 검색은 다음 기준을 중심으로 설계되었습니다.

1. 입력이 정확하지 않아도 결과가 나올 것
2. 짧게 입력해도 원하는 메뉴가 먼저 보일 것


## 4. 그 외 기능
- 검색 흐름을 중심으로 설계했지만,매장 상황에서는 검색 외에도 자주 사용하는 레시피를 바로 확인할 수 있는 흐름이 필요하다고 판단했습니다.
- 이를 위해 시즌 메뉴를 별도로 조회할 수 있는 API와, 카페 구성원이 즐겨찾기에 추가한 레시피 옵션만 조회하는 API를 추가했습니다.
- 이 기능들은 검색을 대체하기보다는, 검색 이후의 반복적인 탐색 비용을 줄일 수 있는 보조 수단으로 설계되었습니다.


# 5. 기술 스택
## 5. Tech Stack

| Category | Stack |
|---------|-------|
| **Language** | ![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white) |
| **Framework & ORM** | ![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white) ![Hibernate](https://img.shields.io/badge/Hibernate-ORM-59666C?style=for-the-badge&logo=hibernate&logoColor=white) |
| **Database** | ![MySQL](https://img.shields.io/badge/MySQL-8.x-4479A1?style=for-the-badge&logo=mysql&logoColor=white) |
| **Authentication** | ![JWT](https://img.shields.io/badge/JWT-Authentication-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white) |
| **Infrastructure** | ![AWS EC2](https://img.shields.io/badge/AWS-EC2-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white) ![Nginx](https://img.shields.io/badge/Nginx-Reverse_Proxy-009639?style=for-the-badge&logo=nginx&logoColor=white) |
| **DevOps** | ![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white) ![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-CI/CD-2088FF?style=for-the-badge&logo=githubactions&logoColor=white) |
| **OS & Environment** | ![Linux](https://img.shields.io/badge/Linux-Ubuntu-FCC624?style=for-the-badge&logo=linux&logoColor=black) |
| **API Documentation** | ![Swagger](https://img.shields.io/badge/Swagger-API_Docs-85EA2D?style=for-the-badge&logo=swagger&logoColor=black) |


