/*
 * micronaut-test-issue-seventy-two
 *
 * Created by ppreston on 27/08/2019.
 */
package micronaut.test.issue.seventy.two.adaptors.db;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import micronaut.test.issue.seventy.two.domain.TestDomain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Singleton
public class TestRepositoryMongo implements TestRepository {

    @Inject
    MongoClient mongoClient;

    @Override
    public Mono<List<TestDomain>> findByDataOne(String data) {
        return Flux.from(collection().find(eq("dataOne", data)))
                   .collectList();
    }

    @Override
    public Mono<List<TestDomain>> findByDataTwo(String data) {
        return Flux.from(collection().find(eq("dataTwo", data)))
                   .collectList();
    }

    @Override
    public Mono<List<TestDomain>> findByDataThree(String data) {
        return Flux.from(collection().find(eq("dataThree", data)))
                   .collectList();
    }



    @Override
    public Mono<TestDomain> create(TestDomain data) {
        return Mono.from(collection().insertOne(data))
                   .map(success -> data);
    }

    private MongoCollection<TestDomain> collection() {
        String databaseName = "test";
        return mongoClient.getDatabase(databaseName)
                          .getCollection("test", TestDomain.class);
    }
}
