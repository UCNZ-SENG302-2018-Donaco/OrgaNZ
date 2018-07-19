package com.humanharvest.organz.server.controller.client;

import java.util.Collection;

import com.humanharvest.organz.ProcedureRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides handlers for requests to these endpoints:
 * - GET /clients/{uid}/procedures
 * - POST /clients/{uid}/procedures
 * - PATCH /clients/{uid}/procedures/{id}
 * - DELETE /clients/{uid}/procedures/{id}
 */
@RestController
public class ClientProceduresController {

    @GetMapping("/clients/{uid}/procedures")
    public ResponseEntity<Collection<ProcedureRecord>> getProceduresForClient() {
        throw new UnsupportedOperationException();
    }

    @PostMapping("/clients/{uid}/procedures")
    public ResponseEntity<Collection<ProcedureRecord>> createProcedureRecord() {
        throw new UnsupportedOperationException();
    }

    @PatchMapping("/clients/{uid}/procedures/{id)")
    public ResponseEntity<ProcedureRecord> modifyProcedureRecord() {
        throw new UnsupportedOperationException();
    }

    @DeleteMapping("/clients/{uid}/procedures/{id}")
    public ResponseEntity deleteProcedureRecord() {
        throw new UnsupportedOperationException();
    }
}
