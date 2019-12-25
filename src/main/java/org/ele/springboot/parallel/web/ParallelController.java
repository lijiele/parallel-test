package org.ele.springboot.parallel.web;

import jdk.nashorn.internal.codegen.CompilerConstants;
import okhttp3.*;
import okhttp3.internal.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/parallel")
public class ParallelController {
    private static final Logger LOG = LoggerFactory.getLogger(ParallelController.class);

    private OkHttpClient client;

    @PostConstruct
    public void init() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        ExecutorService executorService = new ThreadPoolExecutor(200, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), Util.threadFactory("my-OkHttp-Dispatcher", false));
        Dispatcher dispatcher = new Dispatcher(executorService);
        dispatcher.setMaxRequests(1000);
        dispatcher.setMaxRequestsPerHost(1000);
        builder.dispatcher(dispatcher);
        client = builder.build();
        LOG.info("init ParallelController.");
    }

    @RequestMapping(value = "/combined")
    @ResponseBody
    public String combined() throws IOException, ServletException, InterruptedException {

        long start = (new Date()).getTime();
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
//                LOG.info("content:{}", pch.getCall1Content());
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
//                LOG.info("content:{}", pch.getCall2Content());
                response.body().close();
            }
        });
        while (!(pch.isCall1Flag() && pch.isCall2Flag())) {
//            LOG.info("flag1:{}, flag2:{}", pch.isCall1Flag(), pch.isCall2Flag());
            Thread.sleep(200);
        }
        LOG.info("time elapse: {}ms", ((new Date()).getTime() - start));
        return Utils.combineContent(pch.getCall1Content(), pch.getCall2Content());
    }
}
