/*
 * micronaut-test-issue-seventy-two
 *
 * Created by ppreston on 27/08/2019.
 */
package micronaut.test.issue.seventy.two.services;

import lombok.extern.slf4j.Slf4j;
import micronaut.test.issue.seventy.two.adaptors.db.TestRepository;
import micronaut.test.issue.seventy.two.domain.TestDomain;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.function.Function;

@Slf4j
@Singleton
public class TestServiceImpl implements TestService {

    @Inject
    TestRepository repository;

    @Override
    public Mono<TestDomain> testMethodInLambda(String dataOne, String dataTwo, String dataThree) {
        log.info("Checking IN Lambda");
        return checkExists(dataOne, dataTwo, dataThree)
                .log("IN check exists")
                .flatMap(insideLambda(dataOne, dataTwo, dataThree))
                .log("IN done");
    }

    @Override
    public Mono<TestDomain> testMethodOutsideLambda(String dataOne, String dataTwo, String dataThree) {
        log.info("Checking OUSIDE Lambda");
        return checkExists(dataOne, dataTwo, dataThree)
                .log("OUT check exists")
                .flatMap(outsideLambda(dataOne, dataTwo, dataThree))
                .log("OUT done");
    }

    /**
     * OUTSIDE LAMBDA
     * <p>
     * The saveObject Mono is OUTSIDE of the lambda.  THIS WORKS
     */
    private Function<Boolean, Mono<TestDomain>> outsideLambda(String dataOne, String dataTwo, String dataThree) {
        TestDomain       obj  = new TestDomain(null, dataOne, dataTwo, dataThree);
        Mono<TestDomain> send = saveObject(obj);

        return (input) -> {

            log.info("Outside Lambda input: {}", input);
            if (input) {
                send.toProcessor().cancel();
                throw Exceptions.propagate(new RuntimeException("Exception"));
            }

            log.info("Return Mono declared outside");
            return send;
        };
    }

    /**
     * INSIDE LAMBDA
     * <p>
     * The saveObject Mono is INSIDE of the lambda.  THIS DOES NOT WORK.  The injected repository object will return
     * null
     * from the stubbed "create" method, when running IN a lambda, when using the StepVerifier
     */
    private Function<Boolean, Mono<TestDomain>> insideLambda(String dataOne, String dataTwo, String dataThree) {
        TestDomain obj = new TestDomain(null, dataOne, dataTwo, dataThree);
        return (input) -> {
            log.info("Outside Lambda input: {}", input);
            if (input) {
                throw Exceptions.propagate(new RuntimeException("Exception"));
            }

            log.info("Return Mono declared outside");
            return saveObject(obj);
        };
    }

    /**
     * This is the Mono for running the create
     */
    private Mono<TestDomain> saveObject(TestDomain obj) {
        log.info("Return Mono for saving to repository");
        return repository.create(obj);
    }

    private Mono<Boolean> checkExists(String dataOne, String dataTwo, String dataThree) {
        log.info("Checking exists Mono");
        return Flux.from(checkExistsDataOne(dataOne))
                   .mergeWith(checkExistsDataTwo(dataTwo))
                   .mergeWith(checkExistsDataThree(dataThree))
                   .any((exists) -> {
                       log.info("Checking exists: {}", exists);
                       return exists;
                   })
                   .log("exists");
    }

    private Mono<Boolean> checkExistsDataOne(String data) {
        log.info("Checking data one exists Mono");
        return repository.findByDataOne(data)
                         .flatMap((obj) -> {
                             log.info("Data One: {}", (obj.size() > 0));
                             return Mono.just(obj.size() > 0);
                         });
    }

    private Mono<Boolean> checkExistsDataTwo(String data) {
        log.info("Checking data two exists Mono");
        return repository.findByDataTwo(data)
                         .flatMap((obj) -> {
                             log.info("Data Two: {}", (obj.size() > 0));
                             return Mono.just(obj.size() > 0);
                         });
    }

    private Mono<Boolean> checkExistsDataThree(String data) {
        log.info("Checking data three exists Mono");
        return repository.findByDataThree(data)
                         .flatMap((obj) -> {
                             log.info("Data Three: {}", (obj.size() > 0));
                             return Mono.just(obj.size() > 0);
                         });
    }

}
