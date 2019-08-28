/*
 * micronaut-test-issue-seventy-two
 *
 * Created by ppreston on 27/08/2019.
 */
package micronaut.test.issue.seventy.two.services;

import micronaut.test.issue.seventy.two.adaptors.db.TestRepository;
import micronaut.test.issue.seventy.two.domain.TestDomain;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.function.Function;

@Singleton
public class TestServiceImpl implements TestService {

    @Inject
    TestRepository repository;

    @Override
    public Mono<TestDomain> testMethodInLambda(String dataOne, String dataTwo, String dataThree) {
        return checkExists(dataOne, dataTwo, dataThree)
                .flatMap(insideLambda(dataOne, dataTwo, dataThree));
    }

    @Override
    public Mono<TestDomain> testMethodOutsideLambda(String dataOne, String dataTwo, String dataThree) {
        return checkExists(dataOne, dataTwo, dataThree)
                .flatMap(outsideLambda(dataOne, dataTwo, dataThree));
    }

    /**
     * OUTSIDE LAMBDA
     *
     * The saveObject Mono is OUTSIDE of the lambda.  THIS WORKS
     */
    private Function<Boolean, Mono<TestDomain>> outsideLambda(String dataOne, String dataTwo, String dataThree) {
        TestDomain       obj  = new TestDomain(null, dataOne, dataTwo, dataThree);
        Mono<TestDomain> send = saveObject(obj);

        return (input) -> {
            if (input) {
                send.toProcessor().cancel();
                throw Exceptions.propagate(new RuntimeException("Exception"));
            }
            return send;
        };
    }

    /**
     * INSIDE LAMBDA
     *
     * The saveObject Mono is INSIDE of the lambda.  THIS DOES NOT WORK.  The injected repository object will return null
     * from the stubbed "create" method, when running IN a lambda, when using the StepVerifier
     */
    private Function<Boolean, Mono<TestDomain>> insideLambda(String dataOne, String dataTwo, String dataThree) {
        TestDomain obj = new TestDomain(null, dataOne, dataTwo, dataThree);
        return (input) -> {
            if (input) {
                throw Exceptions.propagate(new RuntimeException("Exception"));
            }
            return saveObject(obj);
        };
    }

    /**
     * This is the Mono for running the create
     */
    private Mono<TestDomain> saveObject(TestDomain obj) {
        return repository.create(obj);
    }

    private Mono<Boolean> checkExists(String dataOne, String dataTwo, String dataThree) {
        return Flux.from(checkExistsDataOne(dataOne))
                   .mergeWith(checkExistsDataTwo(dataTwo))
                   .mergeWith(checkExistsDataThree(dataThree))
                   .any((exists) -> exists);
    }

    private Mono<Boolean> checkExistsDataOne(String data) {
        return repository.findByDataOne(data)
                         .flatMap((obj) -> Mono.just(obj.size() > 0));
    }

    private Mono<Boolean> checkExistsDataTwo(String data) {
        return repository.findByDataTwo(data)
                         .flatMap((obj) -> Mono.just(obj.size() > 0));
    }

    private Mono<Boolean> checkExistsDataThree(String data) {
        return repository.findByDataThree(data)
                         .flatMap((obj) -> Mono.just(obj.size() > 0));
    }

}
