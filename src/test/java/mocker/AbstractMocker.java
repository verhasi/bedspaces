package mocker;


import java.util.concurrent.Callable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class AbstractMocker<T> implements Mocker<T>
{
    private Callable<Object> mockMethod, realMethod;
    private Object mockResult, realResult;

    protected void setMethods(Callable<Object> mockMethod, Callable<Object> realMethod){
        this.mockMethod = mockMethod;
        this.realMethod = realMethod;
    }
    @Override
    public  Mocker<T> whenCalled()
    {
        try {
            mockResult = mockMethod.call();
            realResult = callRealNotExpectingException(realMethod);
        }
        catch (Throwable throwableByMock) {
            realResult = callRealExpectingException(realMethod);
        }
        return this;
    }

    @Override
    public void thenAssert(){
        assertResults(mockResult, realResult);
    }
    private <R> void assertResults(R mockResult, R realResult)
    {
        if(null == mockResult){
            assertNull(realResult);
        } else {
            assertThat(realResult, samePropertyValuesAs(mockResult));
        }
    }

    private <R> R callRealExpectingException(Callable<R> real)
    {
        R realResult = null;
        try {
            realResult = real.call();
            fail("Exception expected to be thrown");
        }
        catch (Throwable throwableByReal) {
            assertThat("Expected to be thrown the same class or subclass",
                    throwableByReal.getClass().isAssignableFrom(throwableByReal.getClass()));
        }
        return realResult;
    }

    private <R> R callRealNotExpectingException(Callable<R> real)
    {
        R realResult = null;
        try {
            realResult = real.call();
        }
        catch (Throwable throwableByReal) {
            fail("No exception expected to be thrown");
        }
        return realResult;
    }
}
