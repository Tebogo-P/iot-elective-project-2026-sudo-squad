#include <WiFi.h>
#include <SPI.h>
#include <MFRC522.h>
#include<Adafruit_Fingerprint.h>

const char* ssid = "Wokwi-GUEST";
const char* password = "";

#define SS_PIN 5
#define RST_PIN 22

MFRC522 rfid(SS_PIN, RST_PIN);
HardwareSerial mySerial(2);
Adafruit_Fingerprint finger= Adafruit_Fingerprint(&mySerial);

void setup() {

  Serial.begin(115200);

  Serial.println("Connecting to WiFi...");
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("WiFi Connected!");
  Serial.print("IP Address: ");
  Serial.println(WiFi.localIP());

  SPI.begin();
  rfid.PCD_Init();

  Serial.println("Tap RFID Tag...");

  mySerial.begin(57600, SERIAL_8N1, 16, 17);

  if(finger.verifyPassword()){

    Serial.println("Fingerprint sensor found");
  }
  else{
    Serial.println("Fingerprint sensor not found!");
  }
  }

void loop() {

  if (!rfid.PICC_IsNewCardPresent())
    return;

  if (!rfid.PICC_ReadCardSerial())
    return;

  Serial.print("RFID UID: ");

  for (byte i = 0; i < rfid.uid.size; i++) {
    Serial.print(rfid.uid.uidByte[i], HEX);
    Serial.print(" ");
  }

  Serial.println();
  rfid.PICC_HaltA();
}