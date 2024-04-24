package ru.aston.hw3.testUtils;

import org.jetbrains.annotations.NotNull;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;

public class MockServletInputStream extends ServletInputStream {
    private final ByteArrayInputStream inputStream;

    public MockServletInputStream(String input) {
        this.inputStream = new ByteArrayInputStream(input.getBytes());
    }

    @Override
    public int read() {
        return inputStream.read();
    }

    @Override
    public int read(byte @NotNull [] b, int off, int len) {
        return inputStream.read(b, off, len);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReadListener(ReadListener readListener) {

    }
}

