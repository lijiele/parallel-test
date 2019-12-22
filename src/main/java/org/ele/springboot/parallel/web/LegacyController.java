package org.ele.springboot.parallel.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import java.io.IOException;

@Controller
@RequestMapping("/legacy")
public class LegacyController {
    @RequestMapping(value = "/slow/{id}")
    @ResponseBody
    public String slowApi(@PathVariable Integer id) throws IOException, ServletException {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return String.format("C_%d", id);
    }
}
