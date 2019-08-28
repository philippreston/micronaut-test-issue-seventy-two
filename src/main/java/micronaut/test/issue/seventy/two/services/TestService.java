/*
 * micronaut-test-issue-seventy-two
 *
 * Created by ppreston on 27/08/2019.
 */
package micronaut.test.issue.seventy.two.services;

import micronaut.test.issue.seventy.two.domain.TestDomain;
import reactor.core.publisher.Mono;

public interface TestService {

    Mono<TestDomain> testMethodInLambda(String dataOne, String dataTwo, String dataThree);

    Mono<TestDomain> testMethodOutsideLambda(String dataOne, String dataTwo, String dataThree);
}
