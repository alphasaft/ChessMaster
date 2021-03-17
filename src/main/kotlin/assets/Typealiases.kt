package assets

typealias ExceptionConstructor = (String) -> Throwable
typealias SimpleConstructor<T> = () -> T