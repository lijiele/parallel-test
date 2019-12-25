package org.ele.springboot.parallel.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Date;

@Controller
@RequestMapping("/seq")
public class SequencedController {
    private static final Logger LOG = LoggerFactory.getLogger(SequencedController.class);
    @RequestMapping(value = "/combined")
    @ResponseBody
    public String combined() throws IOException, ServletException {
        long start = (new Date()).getTime();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(String.format(Utils.URL_TPL, 1),
                HttpMethod.GET, null, new ParameterizedTypeReference<String>() {
                });
        String result1 = response.getBody();

        ResponseEntity<String> response2 = restTemplate.exchange(String.format(Utils.URL_TPL, 2),
                HttpMethod.GET, null, new ParameterizedTypeReference<String>() {
                });
        String result2 = response2.getBody();
        LOG.info("time elapse: {}ms", ((new Date()).getTime() - start));
        return Utils.combineContent(result1, result2);
    }
}
