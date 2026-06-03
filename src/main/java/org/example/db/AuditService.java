package org.example.db;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;

public class AuditService {
    private static final AuditService INSTANCE = new AuditService();
    private final String file = "audit.csv";

    private AuditService() {}

    public static AuditService getInstance() { return INSTANCE; }

    public void record(String action) {
        try (FileWriter fw = new FileWriter(file, true)) {
            fw.append(action).append(",").append(Instant.now().toString()).append("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
