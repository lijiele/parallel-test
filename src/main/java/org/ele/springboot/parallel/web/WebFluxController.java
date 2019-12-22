package org.ele.springboot.parallel.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.servlet.ServletException;
import java.io.IOException;

@Controller
@RequestMapping("/webflux")
public class WebFluxController {
    private static final Logger LOG = LoggerFactory.getLogger(WebFluxController.class);
    @RequestMapping(value = "/test")
    @ResponseBody
    public Mono<String> test() throws IOException, ServletException, InterruptedException {
        return Mono.just("test");
    }
    @RequestMapping(value = "/combined")
    @ResponseBody
    public Mono<String> combined() throws IOException, ServletException, InterruptedException {

        ParallelContentHolder pch = new ParallelContentHolder();
        WebClient client = WebClient.create();
        Mono<String> result =
                client.get().uri(String.format(Utils.URL_TPL, 1)).accept(MediaType.APPLICATION_JSON_UTF8).retrieve()
                        .bodyToMono(String.class);
        result.subscribe(string -> {
            pch.setCall1Content(string);
            pch.setCall1Flag(true);
            LOG.info("content1:{}", pch.getCall1Content());
        });

        Mono<String> result2 =
                client.get().uri(String.format(Utils.URL_TPL, 2)).accept(MediaType.APPLICATION_JSON_UTF8).retrieve()
                        .bodyToMono(String.class);
        result2.subscribe(string -> {
            pch.setCall2Content(string);
            pch.setCall2Flag(true);
            LOG.info("content2:{}", pch.getCall2Content());
        });
        while (!(pch.isCall1Flag() && pch.isCall2Flag())) {
//            LOG.info("flag1:{}, flag2:{}", pch.isCall1Flag(), pch.isCall2Flag());
            Thread.sleep(200);
        }
        return Mono.just(Utils.combineContent(pch.getCall1Content(), pch.getCall2Content()));
    }
}
