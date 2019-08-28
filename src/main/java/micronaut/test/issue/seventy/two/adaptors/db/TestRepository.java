/*
 * micronaut-test-issue-seventy-two
 *
 * Created by ppreston on 27/08/2019.
 */
package micronaut.test.issue.seventy.two.adaptors.db;

import micronaut.test.issue.seventy.two.domain.TestDomain;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TestRepository {

    Mono<List<TestDomain>> findByDataOne(String data);

    Mono<List<TestDomain>> findByDataTwo(String data);

    Mono<List<TestDomain>> findByDataThree(String data);

    Mono<TestDomain> create(TestDomain data);

}
