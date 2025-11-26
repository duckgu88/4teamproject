# 배송 관리 시스템

## 프로젝트 개요

- Java 기반의 배송 관리 시스템
- 텍스트 파일에서 배송 주문, 발신자, 수신자 정보 로드 및 배송 상태 관리
- 상호작용을 위한 그래픽 사용자 인터페이스(GUI) 제공
- 지역별 배송 그룹화, 담당 기사 배정, 데이터 검색 기능 지원

## 핵심 패키지

단일 책임 원칙(SRP)에 따라 고유한 책임을 가진 여러 핵심 패키지로 구성

- **main**: 핵심 비즈니스 로직과 데이터 모델 포함 (DeliveryOrder, Sender, Receiver, DeliverySystem 등)
- **mgr**: 파일 데이터 관리 및 객체 생성을 위한 범용 프레임워크 제공, 팩토리 패턴으로 재사용성 증대
- **guitool**: GUI 구성을 위한 모든 Java Swing 컴포넌트 포함, 데이터 시각화 및 사용자 상호작용 처리
- **search**: 이름, 지역, 송장 번호 등 다양한 조건의 데이터 검색 기능 구현

## 패키지 간 상호작용

### 데이터 흐름
- mgr → main → guitool / search의 단방향 구조
- mgr가 파일을 읽어 main의 객체 생성 및 초기화
- guitool과 search는 main의 데이터를 이용해 기능 수행

### 패키지별 상세 역할

#### 1. main 패키지 (중앙 허브)
- **역할**: 핵심 데이터 클래스(DeliveryOrder 등)와 이를 총괄하는 싱글톤 DeliverySystem 포함
- **상호작용**:
    - mgr 의존: DeliverySystem이 mgr.Manager를 사용해 객체 생성, 대상 클래스는 mgr.Manageable 인터페이스 구현
    - 데이터 제공: getter 메소드를 통해 UI 표시 및 검색에 필요한 데이터를 외부에 제공

#### 2. mgr 패키지 (데이터 로더)
- **역할**: 파일 데이터를 객체로 변환하는 재사용 가능한 로딩 프레임워크
- **상호작용**:
    - main.DeliverySystem의 Manager.readAll 호출 시, Factory를 통해 Manageable 구현체를 생성하고 파일 내용 주입

#### 3. guitool 패키지 (사용자 인터페이스)
- **역할**: Swing 기반의 모든 GUI 화면 생성 및 관리
- **상호작용**:
    - main 의존: DeliverySystem의 데이터 목록(Dlist)을 JTable에 채우고, 메소드를 호출해 데이터 상태 변경
    - search 의존: 검색 시 search.Searchable 구현체를 생성하고 Matcher.findMatches를 호출해 결과 획득

#### 4. search 패키지 (검색 로직)
- **역할**: 다양한 검색 기준을 Searchable 인터페이스와 구현체로 정의
- **상호작용**:
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

### guitool.GUIApp, guitool.MainMenu, 등
- 사용자 인터페이스를 생성하고 관리하는 클래스
- GUIApp은 메인 프레임 설정, 그 외 클래스는 각 UI 화면을 담당
- DeliverySystem과 상호작용하여 데이터 표시 및 기능 트리거
