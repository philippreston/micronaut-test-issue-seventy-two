# Micronaut Test Issue With StepVerifier

To recreate run 

```
./gradlew build
```

The following test will fail:

```
micronaut.test.issue.seventy.two.services.TestServiceTest > test in lambda FAILED
    org.spockframework.mock.TooFewInvocationsError at TestServiceTest.groovy:37
```

This is the issue where the injected Mock Bean returns null for the stubbed method, rather than 
the object that should be returned.  This is registered as having "Too few invocations".
