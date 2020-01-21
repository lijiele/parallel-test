package org.ele.springboot.parallel;

import org.ele.springboot.parallel.web.Utils;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author ele on 21/01/20
 * @project parallel
 */
public class ReactorTest {
    @Test
    public void test1() {
        Mono<String> noData = Mono.empty();

        Mono<String> data1 = Mono.just("foo");
        Mono<String> data2 = Mono.just("bar");

        data1.zipWith(data2, Utils::combineContent).subscribe(System.out::println);
    }
}
