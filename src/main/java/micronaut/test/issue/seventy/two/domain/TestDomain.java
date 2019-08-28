/*
 * micronaut-test-issue-seventy-two
 *
 * Created by ppreston on 27/08/2019.
 */
package micronaut.test.issue.seventy.two.domain;

import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Introspected
public class TestDomain {

    @BsonId
    String objectId;

    @NotNull
    String dataOne;

    @NotNull
    String dataTwo;

    @NotNull
    String dataThree;
}
