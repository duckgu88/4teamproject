# 배송 관리 시스템

## 프로젝트 개요

- Java 기반의 배송 관리 시스템
- 텍스트 파일에서 배송 주문, 발신자, 수신자 정보 로드 및 배송 상태 관리
- 상호작용을 위한 그래픽 사용자 인터페이스(GUI) 제공
- 지역별 배송 그룹화, 담당 기사 배정, 데이터 검색 기능 지원

## 주요 기능

### 관리자 기능
- 배송 현황 관리: 전체 배송 목록 실시간 조회, 날짜 갱신을 통한 상태 업데이트
- 데이터 필터링 및 정렬: 발신자, 수신자, 배송 상태 등 다양한 조건으로 데이터 정렬 및 필터링
- 지역별 현황 요약: 지역별 배송 건수 및 담당자 시각화
- 주문 정보 관리: 신규 주문 등록, 기존 주문 주소 수정 및 삭제

### 사용자(게스트) 기능
- 배송 조회: 송장 번호 또는 수령인 이름으로 배송 정보를 검색하고, 상세 정보(배달 기사, 운송 회사 포함)를 표 형태로 조회

## 핵심 패키지

단일 책임 원칙(SRP)에 따라 고유한 책임을 가진 여러 핵심 패키지로 구성
- main: 핵심 비즈니스 로직과 데이터 모델 포함 (DeliveryOrder, Sender, Receiver, DeliverySystem 등)
- mgr: 파일 데이터 관리 및 객체 생성을 위한 범용 프레임워크 제공, 팩토리 패턴으로 재사용성 증대
- guitool: GUI 구성을 위한 모든 Java Swing 컴포넌트 포함, 데이터 시각화 및 사용자 상호작용 처리
- search: 이름, 지역, 송장 번호 등 다양한 조건의 데이터 검색 기능 구현

## 패키지 간 상호작용

### 데이터 흐름
- mgr → main → guitool / search의 단방향 구조
- mgr가 파일을 읽어 main의 객체 생성 및 초기화
- guitool과 search는 main의 데이터를 이용해 기능 수행

### 패키지별 상세 역할

#### 1. main 패키지 (중앙 허브)
- 역할: 핵심 데이터 클래스(DeliveryOrder 등)와 이를 총괄하는 싱글톤 DeliverySystem 포함
- 상호작용:
    - mgr 의존: DeliverySystem이 mgr.Manager를 사용해 객체 생성, 대상 클래스는 mgr.Manageable 인터페이스 구현
    - 데이터 제공: getter 메소드를 통해 UI 표시 및 검색에 필요한 데이터를 외부에 제공

#### 2. mgr 패키지 (데이터 로더)
- 역할: 파일 데이터를 객체로 변환하는 재사용 가능한 로딩 프레임워크
- 상호작용:
    - main.DeliverySystem의 Manager.readAll 호출 시, Factory를 통해 Manageable 구현체를 생성하고 파일 내용 주입

#### 3. guitool 패키지 (사용자 인터페이스)
- 역할: Swing 기반의 GUI 화면 구성 및 사용자 상호작용 처리, `UITheme`을 통한 일관된 디자인 관리
- 상호작용:
    - `MainFrame`이 `CardLayout`을 사용하여 `LoginScreen`, `ShippingPage`, `InquiryPage` 등 여러 화면을 전환
    - 각 페이지는 `DeliverySystem`의 데이터를 `JTable`에 표시하고, 사용자 입력에 따라 데이터 정렬, 필터링, 수정, 삭제 기능 수행
    - `InquiryPage`는 `InquiryPresenter`를 통해 GUI 로직과 비즈니스 로직 분리

#### 4. search 패키지 (검색 로직)
- 역할: 다양한 검색 기준을 Searchable 인터페이스와 구현체로 정의
- 상호작용:
    - main 의존: 모든 Searchable 구현체는 DeliveryOrder 객체를 받아 getter로 데이터에 접근, 검색 조건 일치 여부 확인

## 상세 클래스 설명

### main.DeliverySystem
- 싱글턴 패턴으로 구현된 중앙 컨트롤러
- 앱 시작 시 데이터 초기화, 데이터 접근 및 조작 메소드 제공
- updateDeliveryStatuses 메소드로 시간 경과에 따른 배송 상태 업데이트 기능 수행

### main.DeliveryOrder
- 하나의 배송 단위를 나타내는 데이터 클래스
- Sender, Receiver 객체와 송장 번호 등 관련 정보 집계 및 관리

### main.Receiver
- 수신자 정보와 배송 상태를 담당하는 클래스
- read 메소드로 파일에서 주소 정보를 파싱
- decrementDeliveryDays 메소드는 DeliverySystem 호출로 배송 진행 상황 업데이트

### mgr.Manager and mgr.Factory
- 팩토리 패턴의 재사용 가능한 구현을 제공하는 클래스
- Manager는 Factory를 이용해 파일 내용으로 객체를 생성
- DeliverySystem의 데이터 로딩 과정에 사용됨

### guitool.MainFrame
- `CardLayout`을 사용하는 메인 프레임으로, 앱의 여러 페이지(화면)를 관리하는 컨테이너

### guitool.LoginScreen
- 앱 시작 시 표시되는 초기 화면. 관리자 로그인 시 페이지를 전환하고, 비회원(게스트) 조회 시 `GuestLogin` 조회 화면(JFrame)을 실행

### guitool.GuestLogin
- 게스트(비회원)를 위한 배송 조회 화면. 송장 번호 또는 수령인 이름으로 주문을 검색할 수 있으며, 검색 결과는 테이블 형태로 표시됩니다. drivers.txt와 senders.txt 파일의 정보를 바탕으로 배달기사와 운송회사 정보를 함께 보여주는 기능이 포함

### guitool.ShippingPage
- 전체 배송 목록을 `JTable`로 표시하는 배송 관리 화면
- 정렬, 필터링, 지역별 요약, 날짜 갱신 등 관리자 기능 제공

### guitool.InquiryPage
- 다양한 조건으로 주문을 검색하고, 주소 수정 및 주문 삭제를 수행하는 주문 관리 화면
- `InquiryPresenter`와 협력하여 MVP(Model-View-Presenter) 패턴 적용

### guitool.WaybillDialog
- 개별 배송 건의 상세 정보를 운송장 형태로 보여주는 팝업 다이얼로그

### guitool.UITheme
- 앱의 전체적인 디자인(색상, 폰트, 컴포넌트 스타일)을 정의하는 유틸리티 클래스
- `createStyledButton`, `createStyledTable` 등 팩토리 메소드를 통해 일관된 UI 컴포넌트 생성 지원

## 핵심 코드 로직

### 1. Factory 패턴을 이용한 데이터 로딩 (`mgr.Manager`)
- 역할: `mgr.Manager`의 `readAll` 메소드는 `Factory` 인터페이스를 사용하여 파일 데이터를 객체로 생성. `DeliverySystem`은 `SenderFactory`, `ReceiverFactory` 등 구체적인 팩토리를 `Manager`에 전달
- 상호작용:
    - `Manager`는 파일 각 줄 읽을 때마다 `Factory.create()` 호출하여 `Manageable` 타입 객체(예: `Sender`) 생성 및 `read()` 메소드로 파일 내용 채움.
    - `Manager` 코드 수정 없이 새로운 데이터 타입 추가 유연성 확보

### 2. 배송 상태 자동 업데이트 (`main.DeliverySystem`)
- 역할: `updateDeliveryStatuses` 메소드를 통한 날짜 경과에 따른 배송 주문 상태 자동 관리. 실제 배송 과정 시뮬레이션 및 배송 현황 추적 기능 제공
- 상호작용:
    - 모든 `DeliveryOrder` 순회, `Receiver` 객체의 남은 배송일(`daysUntilDelivery`) 1씩 감소
    - 배송일 0 이하 시, 주문 상태 "배송전" -> "배송중" -> "배송완료" 순서로 변경

### 3. 다형성을 활용한 검색 기능 (`search` 패키지)
- 역할: `Searchable` 인터페이스 활용, 다양한 검색 기준 통일된 방식으로 처리. `PhoneSearcher`, `RegionSearcher` 등 `Searchable` 구현
- 상호작용:
    - `SearchPresenter`는 요청에 맞는 `Searchable` 구현체(예: `PhoneSearcher`) 생성, `Matcher.findMatches` 메소드에 전달
    - `Matcher`는 `Searchable`의 `matches()` 메소드 호출, 조건에 맞는 `DeliveryOrder` 필터링
    - 새로운 검색 기준 추가 시 `Searchable` 구현 클래스만 추가하여 코드 확장 용이

## 적용된 설계 패턴

- 싱글턴 패턴 (Singleton Pattern)
  - `DeliverySystem` 객체를 유일하게 생성하여, 애플리케이션 전역에서 데이터 일관성을 유지하며 접근하도록 설계

- 팩토리 패턴 (Factory Pattern)
  - `mgr` 패키지에서 `Factory` 인터페이스를 통해 `Sender`, `Receiver` 등 다양한 종류의 객체 생성을 표준화하고 `Manager` 클래스와의 결합도를 낮춤

- 전략 패턴 (Strategy Pattern)
  - `search` 패키지에서 `Searchable` 인터페이스를 '전략'으로 사용하여, `Matcher`의 코드 변경 없이 다양한 검색 로직(전략)을 유연하게 교체 및 확장

- MVP (Model-View-Presenter) 패턴
  - `InquiryPage`(View)와 `InquiryPresenter`(Presenter)를 분리하여 GUI 로직과 비즈니스 로직을 분리, 코드의 테스트 용이성과 유지보수성 향상
