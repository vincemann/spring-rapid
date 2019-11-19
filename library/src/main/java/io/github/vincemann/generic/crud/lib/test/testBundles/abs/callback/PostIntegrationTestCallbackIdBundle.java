package io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class PostIntegrationTestCallbackIdBundle<Id extends Serializable> {
    private Id id;
    private ResponseEntity<String> responseEntity;
}
