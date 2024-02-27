package com.github.vincemann.springrapid.core.util;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ResourceUtils {

    /**
     * Reads a resource into a String
     */
    public static String toStr(Resource resource) throws IOException {

        String text = null;
        try (Scanner scanner = new Scanner(resource.getInputStream(), StandardCharsets.UTF_8)) {
            text = scanner.useDelimiter("\\A").next();
        }

        return text;
    }
}
