#include <BluetoothSerial.h>

// BluetoothSerial 객체 생성
BluetoothSerial SerialBT;

// 초음파 센서 핀 정의
const int trigPin = D9;
const int echoPin = D8;

void setup() {
    Serial.begin(115200); // 시리얼 통신 시작
    SerialBT.begin("ESP32_Ultrasonic"); // 블루투스 이름 설정
    pinMode(trigPin, OUTPUT);
    pinMode(echoPin, INPUT);

}

void loop() {
  if (SerialBT.connected()) {                            // 블루투스가 연결되었다면
    Serial.println("연결되었습니다.");                     // "연결되었습니다." 출력

    long duration;
    float distance;

    // 초음파 센서 작동
    digitalWrite(trigPin, LOW);
    delayMicroseconds(2);
    digitalWrite(trigPin, HIGH);
    delayMicroseconds(10);
    digitalWrite(trigPin, LOW);
    
    duration = pulseIn(echoPin, HIGH);
    distance = (duration * 0.034) / 2; // 거리 계산 (cm)

    // 거리 값을 블루투스를 통해 전송
    //SerialBT.print(distance);
    //SerialBT.print("\n");

     // 거리 조건에 따라 신호 전송
    if (distance <= 30) { // 예: 거리 20cm 이하일 때
        SerialBT.println("1"); // 신호 전송
    } else {
        SerialBT.println("0"); // 신호 전송
    }

    Serial.print("거리: ");
    Serial.println(distance);
    

  } else {                                                 // 블루투스가 연결되지 않았다면
    Serial.println("연결되지 않았습니다.");                // "연결되지 않았습니다." 출력
  }
    
    delay(1000); // 1초마다 거리 전송
}