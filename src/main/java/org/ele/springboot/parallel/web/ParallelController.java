package org.ele.springboot.parallel.web;

import jdk.nashorn.internal.codegen.CompilerConstants;
import okhttp3.*;
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

@Controller
@RequestMapping("/parallel")
public class ParallelController {
    private static final Logger LOG = LoggerFactory.getLogger(ParallelController.class);

    private OkHttpClient client = new OkHttpClient();
    @RequestMapping(value = "/combined")
    @ResponseBody
    public String combined() throws IOException, ServletException, InterruptedException {

        Request request = new Request.Builder().url(String.format(Utils.URL_TPL, 1)).build();
        Call call = client.newCall(request);
        ParallelContentHolder pch = new ParallelContentHolder();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LOG.error("onFailure error", e);
            }

            public void onResponse(Call call, Response response) throws IOException {
                pch.setCall1Content(response.body().string());
                pch.setCall1Flag(true);
                LOG.info("content:{}", pch.getCall1Content());
                response.body().close();
            }
        });
        Request request2 = new Request.Builder().url(String.format(Utils.URL_TPL, 2)).build();
        Call call2 = client.newCall(request2);
        call2.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LOG.error("onFailure error", e);
            }

            public void onResponse(Call call, Response response) throws IOException {
                pch.setCall2Content(response.body().string());
                pch.setCall2Flag(true);
                LOG.info("content:{}", pch.getCall2Content());
                response.body().close();
            }
        });
        while(!(pch.isCall1Flag() && pch.isCall2Flag())) {
//            LOG.info("flag1:{}, flag2:{}", pch.isCall1Flag(), pch.isCall2Flag());
            Thread.sleep(200);
        }
        return Utils.combineContent(pch.getCall1Content(), pch.getCall2Content());
    }
}
