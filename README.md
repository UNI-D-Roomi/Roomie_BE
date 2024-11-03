# [2024 Uni-Dthon Team 11: <u>**Roomie**</u> - BE]
- Demo Video: https://youtu.be/VsJinTHyXHQ
- Demo Service(It could be closed soon!): https://roomie-service.netlify.app/

# 1. How we Impl BE-Server?
### BE Architecture
  ![image](https://github.com/user-attachments/assets/d86a799f-f82a-4692-a930-2492e4a232a0)
### Core Feature
1. 깨끗한 방인지 판단 후 점수 매기기/코멘트 생성
2. 기상청 API로 날씨 불러오기
3. Roomie 키우기 점수 랭킹 기능
4. Roomie Hunger Gage 시간 비례 줄어들기
  - 단위 시간(StaticValue로 `Stride`라 명명함)에 맞춰 현재 Roomie를 조회할 때마다 Hunger Gage가 마지막 Feed한 시점과 차이에 정비례하여 줄어듭니다.
  - `Stride`는 유동적으로 조정할 수 있도록 관리합니다.
6. Roomie 랜덤 메세지 불러오기
  - 청소 관련 팁/Roomie의 캐릭터성 메세지/날씨 기반 청소 팁/사용자 맞춤 상호작용으로 총 4개 카테고리로 Roomie의 랜덤 메세지를 정의했습니다.
6. 이미지 Resizing
  - 최근 스마트폰 카메라의 성능이 좋아져 업로드 파일이 20~30MB 가량의 크기가 나와 Latency가 지나치게 큰 문제가 있었습니다.
  - 이를 해결하기 위해 이미지 파일 압축으로 해상도 손실이 있더라도 GPT 모델 추론에 문제가 없는지 확인 후 이미지 파일 압축을 결정했습니다.
  - ***Latency를 60sec 이상 -> 4~6sec 로 줄였습니다.***: `thumbnailator` 의존성을 통해 이미지 크기를 300KB 이하로 제한하여 위 문제를 해결했습니다.
<br><br><br>
# 2. Service Information
## 2-a. Service Background
![Uni-D Roomie Presentation-2](https://github.com/user-attachments/assets/2a45450c-2c44-4a07-8867-d9180031c5e4)

![Uni-D Roomie Presentation-3](https://github.com/user-attachments/assets/9348074d-0a45-468d-a4fc-af5868a22acf)
<br><br><br>
## 2-b. Main Feature
![Uni-D Roomie Presentation-4](https://github.com/user-attachments/assets/e93dc1ba-c757-4ccb-a0b0-9475e4cf51ab)

![Uni-D Roomie Presentation-5](https://github.com/user-attachments/assets/ada56762-83b2-4d88-b815-ffb543386733)

![Uni-D Roomie Presentation-6](https://github.com/user-attachments/assets/cfcf0a10-4184-4e77-bfd4-80513f313589)

![Uni-D Roomie Presentation-7](https://github.com/user-attachments/assets/7c2ab86a-2fc4-4a25-87cf-6aef39c59d6e)

![Uni-D Roomie Presentation-8](https://github.com/user-attachments/assets/363dc1b9-1a11-4807-a0fa-3c3d6d064ab7)

![Uni-D Roomie Presentation-9](https://github.com/user-attachments/assets/1008b30e-c1d7-4f6c-89cb-c4c1f1e8a192)

![Uni-D Roomie Presentation-10](https://github.com/user-attachments/assets/cc3fb2a7-ca01-4630-8d90-5c2e6896f84d)

![Uni-D Roomie Presentation-11](https://github.com/user-attachments/assets/5ee765f3-9681-4653-9b47-dea1be29824b)

![Uni-D Roomie Presentation-12](https://github.com/user-attachments/assets/ff6cdd4b-b060-4a07-8a35-99ccb5228a20)
<br><br><br>
## 2-c. Tech Stack
![Uni-D Roomie Presentation-13](https://github.com/user-attachments/assets/30542478-ae44-4aee-86e4-5b0664a7f324)

## 2-d. Service Architecture
![Uni-D Roomie Presentation-14](https://github.com/user-attachments/assets/e8368580-5713-4b99-bb85-62714c6c6642)
