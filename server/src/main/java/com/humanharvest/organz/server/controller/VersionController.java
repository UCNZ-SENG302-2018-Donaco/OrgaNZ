package com.humanharvest.organz.server.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersionController {

    /**
     * Returns the version as loaded from a file.
     */
    @GetMapping("/version")
    public ResponseEntity<String> getVersion() {
        File versionFile = new File("version");
        if (versionFile.exists()) {
            try(Scanner scanner = new Scanner(new File("version"))) {
                String content = scanner.useDelimiter("\\Z").next();
                return new ResponseEntity<>(content, HttpStatus.OK);
            } catch (FileNotFoundException e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
