package micronaut.test.issue.seventy.two.services

import io.micronaut.test.annotation.MicronautTest
import io.micronaut.test.annotation.MockBean
import micronaut.test.issue.seventy.two.adaptors.db.TestRepository
import micronaut.test.issue.seventy.two.adaptors.db.TestRepositoryMongo
import micronaut.test.issue.seventy.two.domain.TestDomain
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

import javax.inject.Inject
/*
 * micronaut-test-issue-seventy-two 
 *
 * Created by ppreston on 27/08/2019.
 */

@MicronautTest
class TestServiceTest extends Specification {

    @Inject
    TestService testService

    @Inject
    TestRepository testRepository

    // THIS TEST FAILS - DEMONSTRATING THE ISSUE
    def "test in lambda"() {

        given: "a few data items"
        String dataOne = "111111"
        String dataTwo = "222222"
        String dataThree = "333333"

        when: "calling the method that uses the IN LAMBDA path"
        Mono<TestDomain> m = testService.testMethodInLambda(dataOne, dataTwo, dataThree)

        then: "create the stub methods"
        1 * testRepository.findByDataOne(_) >> { Mono.just([]) }
        1 * testRepository.findByDataTwo(_) >> { Mono.just([]) }
        1 * testRepository.findByDataThree(_) >> { Mono.just([]) }

        // SPOCK INDICATES THIS METHOD IS NEVER CALLED - BUT A "FORM" OF IT IS CALLED BUT RETURNS NULL
        1 * testRepository.create(_) >> { TestDomain d -> Mono.just(d) }

        and: "use the step verifier for testing the Mono execution - WILL FAIL"
        StepVerifier.create(m).expectNextMatches({ TestDomain result ->
            result.dataOne == dataOne &&
                    result.dataTwo == dataTwo &&
                    result.dataThree == dataThree
        }).verifyComplete()

    }

    def "test in lambda (no step verifier)"() {

        given: "a few data items"
        String dataOne = "111111"
        String dataTwo = "222222"
        String dataThree = "333333"

        when: "calling the method that uses the IN LAMBDA path - blocking this time"
        TestDomain result = testService.testMethodInLambda(dataOne, dataTwo, dataThree).block()

        then: "create the stub methods"
        1 * testRepository.findByDataOne(_) >> { Mono.just([]) }
        1 * testRepository.findByDataTwo(_) >> { Mono.just([]) }
        1 * testRepository.findByDataThree(_) >> { Mono.just([]) }
        1 * testRepository.create(_) >> { TestDomain d -> Mono.just(d) }

        and: "NOT using the the step verifier - WILL PASS"
        result.dataOne == dataOne
        result.dataTwo == dataTwo
        result.dataThree == dataThree
    }


    def "test outside lambda"() {

        given: "a few data items"
        String dataOne = "111111"
        String dataTwo = "222222"
        String dataThree = "333333"

        when: "calling the method that uses the OUT LAMBDA path"
        Mono<TestDomain> m = testService.testMethodOutsideLambda(dataOne, dataTwo, dataThree)

        then: "create the stub methods"
        1 * testRepository.findByDataOne(_) >> { Mono.just([]) }
        1 * testRepository.findByDataTwo(_) >> { Mono.just([]) }
        1 * testRepository.findByDataThree(_) >> { Mono.just([]) }
        1 * testRepository.create(_) >> { TestDomain d -> Mono.just(d) }

        and: "use the step verifier for testing the Mono execution - WILL PASS"
        StepVerifier.create(m).expectNextMatches({ TestDomain result ->
            result.dataOne == dataOne &&
                    result.dataTwo == dataTwo &&
                    result.dataThree == dataThree
        }).verifyComplete()
    }


    @MockBean(TestRepositoryMongo)
    TestRepository testRepository() {
        Mock(TestRepository)
    }

}
