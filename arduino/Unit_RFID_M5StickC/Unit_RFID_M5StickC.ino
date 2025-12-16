/*
 * SPDX-FileCopyrightText: 2024 M5Stack Technology CO LTD
 *
 * - https://github.com/m5stack/M5Unit-UHF-RFID/blob/master/src/UNIT_UHF_RFID.cpp
 * - https://shop.m5stack.com/products/uhf-rfid-unit-jrd-4035
 */

#include <M5StickC.h>
#include <M5GFX.h>

#include "UNIT_UHF_RFID.h"

M5GFX display;
M5Canvas canvas(&display);
Unit_UHF_RFID uhf;

String info = "";

// uint8_t epcTarget[]  = {0xe2, 0x00, 0x47, 0x10, 0x91, 0x70, 0x68, 0x21, 0x2a, 0x44, 0x01, 0x0d};
// String epcTargetStr = "e2004710917068212a44010d";
// boolean logged = false;

void setup() {
    M5.begin();
    // Serial2.begin(unsigned long baud, uint32_t config, int8_t rxPin, int8_t txPin, bool invert) 
    // uhf.begin(HardwareSerial *serial = &Serial2, int baud=115200, uint8_t RX = 16, uint8_t TX = 17, bool debug = false);
    uhf.begin(&Serial2, 115200, 33, 32, false);
    //   uhf.begin();
    while (1) {
        info = uhf.getVersion();
        if (info != "ERROR") {
            Serial.println(info);
            break;
        }
    }

    // max: 26dB
    uhf.setTxPower(2600);

    display.begin();
    canvas.setRotation(3);
    canvas.setColorDepth(1);  // mono color
    canvas.setFont(&fonts::efontCN_14);
    canvas.createSprite(display.width(), display.height());
    canvas.setTextSize(1);
    canvas.setPaletteColor(1, GREEN);
    canvas.setTextScroll(true);
    canvas.println(info);
    canvas.println("1.BtnA Write or Read Card Info");
    canvas.println("2.BtnB Select Card EPC");
    canvas.pushSprite(0, 0);
}

void log(String info) {
    Serial.println(info);
    canvas.println(info);
    canvas.pushSprite(0, 0);
}

void loop() {
    
    uint8_t numberOfCards = uhf.pollingOnce();
    // uint8_t numberOfCards = uhf.pollingMultiple(10);
    log("Num tags: " + String(numberOfCards));
    if(numberOfCards > 0) {
        for (int i = 0; i < numberOfCards; i++) {
            log(String(i) + "| epc_str: " + String(uhf.cards[i].epc_str).substring(10) + "| RSSI: " + String(uhf.cards[i].rssi));
            // log(String(i) + "| epc_str: " + String(uhf.cards[i].epc_str) + "| RSSI: " + String(uhf.cards[i].rssi));
            // log(String(i) + " | RSSI: " + String(uhf.cards[i].rssi));
        }
    }

    // log(String(millis()));
    delay(1);

    M5.update();
}