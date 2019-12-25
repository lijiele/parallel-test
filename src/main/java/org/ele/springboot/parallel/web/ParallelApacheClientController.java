package org.ele.springboot.parallel.web;

import okhttp3.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.concurrent.ThreadFactory;

@Controller
@RequestMapping("/parallelApache")
public class ParallelApacheClientController {
    private static final Logger LOG = LoggerFactory.getLogger(ParallelApacheClientController.class);

    private CloseableHttpAsyncClient httpclient;

    @PostConstruct
    public void init() {
//        ThreadFactory tf = new ThreadFactory() {
//            @Override public Thread newThread(Runnable runnable) {
//                Thread result = new Thread(runnable, "my-apache-client");
//                result.setDaemon(false);
//                return result;
//            }
//        };

        ThreadFactory tf2 = (runnable) -> {
          Thread result = new Thread(runnable, "my-apache-client2");
          result.setDaemon(false);
          return result;
        };
        httpclient = HttpAsyncClientBuilder.create()
                .setMaxConnTotal(1000)
                .setMaxConnPerRoute(1000)
                .setThreadFactory(tf2).build();
    }
    @RequestMapping(value = "/combined")
    @ResponseBody
    public String combined() throws IOException, ServletException, InterruptedException, URISyntaxException {
        long start = (new Date()).getTime();
        ParallelContentHolder pch = new ParallelContentHolder();
        httpclient.start();
        final HttpGet request = new HttpGet();
        request.setURI(new URI(String.format(Utils.URL_TPL, 1)));
        httpclient.execute(request, new FutureCallback<HttpResponse>() {
            public void completed(final HttpResponse response) {
                String body = null;
                try {
                    body = EntityUtils.toString(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                pch.setCall1Content(body);
                pch.setCall1Flag(true);
//                LOG.info("content:{}", body);
            }

            public void failed(final Exception ex) {
                LOG.error("failed", ex);
            }

            public void cancelled() {
                LOG.error("cancelled");
            }

        });

        final HttpGet request2 = new HttpGet();
        request2.setURI(new URI(String.format(Utils.URL_TPL, 2)));
        httpclient.execute(request2, new FutureCallback<HttpResponse>() {
            public void completed(final HttpResponse response) {
                String body = null;
                try {
                    body = EntityUtils.toString(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                pch.setCall2Content(body);
                pch.setCall2Flag(true);
//                LOG.info("content2:{}", body);
            }

            public void failed(final Exception ex) {
                LOG.error("failed", ex);
            }

            public void cancelled() {
                LOG.error("cancelled");
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
