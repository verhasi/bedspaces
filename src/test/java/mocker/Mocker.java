package mocker;

public interface Mocker<T>
{
    T getMock();
    Mocker<T> whenCalled();
    void thenAssert();
}
