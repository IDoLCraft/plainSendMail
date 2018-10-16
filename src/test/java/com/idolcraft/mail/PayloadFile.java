package com.idolcraft.mail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PayloadFile {
    private final File file;

    public PayloadFile(File file) {
        this.file = file;
    }

    public PayloadFile(String path) {
        this(Paths.get(path).toFile());
    }

    public byte[] bytes() throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    public String contentAsString() throws IOException {
        return new String(bytes(), Charset.forName("UTF-8"));
    }
}
