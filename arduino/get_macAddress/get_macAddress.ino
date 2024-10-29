#include <WiFi.h>

void setup() {
  Serial.begin(115200); // 시리얼 통신 시작
  WiFi.begin(); // Wi-Fi 초기화
  delay(1000);  // 초기화 대기

}

void loop() {
  Serial.println(WiFi.macAddress());
  delay(5000); // 1초마다 거리 전송
}
