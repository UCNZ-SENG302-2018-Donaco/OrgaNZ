package com.humanharvest.organz.server.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class VersionController {

    private static final Logger LOGGER = Logger.getLogger(VersionController.class.getName());

    /**
     * Returns the version as loaded from a file.
     */
    @GetMapping("/version")
    public ResponseEntity<String> getVersion() {
        File versionFile = new File("version");
        if (versionFile.exists()) {
            try (Scanner scanner = new Scanner(new File("version"))) {
                String content = scanner.useDelimiter("\\Z").next();
                return new ResponseEntity<>(content, HttpStatus.OK);
            } catch (FileNotFoundException e) {
                LOGGER.log(Level.INFO, e.getMessage(), e);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/")
    public ModelAndView method() {
        return new ModelAndView("redirect:https://eng-git.canterbury.ac.nz/seng302-2018/team-700/blob/development/doc/user_manual.md#organz-user-manual");

    }
}
