package com.suntek.sample.sc5sk6;

/**
 * Builds STC8G1K08A WS2812B LED serial protocol frames (VER=1) for SC5 / SK6
 * on /dev/ttyS1 at 115200 bps 8N1: AA 55 | VER | CMD | LEN | PAYLOAD | CRC16_L | CRC16_H.
 */
public final class Ws2812bLedFrameBuilder {
    public static final String SERIAL_PORT = "/dev/ttyS1";
    public static final int BAUD_RATE = 115200;
    private static final int PROTOCOL_VERSION = 0x01;

    private Ws2812bLedFrameBuilder() {}

    public static byte[] buildStaticColorFrame(int r, int g, int b, int brightness) {
        return buildFrame(0x01, new byte[] {
            (byte) (r & 0xFF),
            (byte) (g & 0xFF),
            (byte) (b & 0xFF),
            (byte) (brightness & 0xFF),
        });
    }

    public static byte[] buildSetBrightnessFrame(int brightness) {
        return buildFrame(0x03, new byte[] { (byte) (brightness & 0xFF) });
    }

    public static byte[] buildSetLedCountFrame(int count) {
        int clamped = Math.max(1, Math.min(1024, count));
        return buildFrame(0x04, new byte[] { (byte) (clamped & 0xFF), (byte) ((clamped >> 8) & 0xFF) });
    }

    public static byte[] buildSetPowerFrame(boolean on) {
        return buildFrame(0x07, new byte[] { (byte) (on ? 0x01 : 0x00) });
    }

    public static byte[] buildQueryStatusFrame() {
        return buildFrame(0x08, new byte[0]);
    }

    public static byte[] buildFrame(int cmd, byte[] payload) {
        int len = payload == null ? 0 : payload.length;
        byte[] body = new byte[3 + len];
        body[0] = (byte) PROTOCOL_VERSION;
        body[1] = (byte) (cmd & 0xFF);
        body[2] = (byte) (len & 0xFF);
        if (len > 0) {
            System.arraycopy(payload, 0, body, 3, len);
        }
        int crc = crc16Modbus(body, 0, body.length);
        byte[] frame = new byte[2 + body.length + 2];
        frame[0] = (byte) 0xAA;
        frame[1] = (byte) 0x55;
        System.arraycopy(body, 0, frame, 2, body.length);
        frame[2 + body.length] = (byte) (crc & 0xFF);
        frame[2 + body.length + 1] = (byte) ((crc >> 8) & 0xFF);
        return frame;
    }

    public static int crc16Modbus(byte[] data, int offset, int length) {
        int crc = 0xFFFF;
        for (int i = offset; i < offset + length; i++) {
            crc ^= (data[i] & 0xFF);
            for (int b = 0; b < 8; b++) {
                crc = (crc & 1) != 0 ? ((crc >> 1) ^ 0xA001) : (crc >> 1);
            }
        }
        return crc & 0xFFFF;
    }
}
