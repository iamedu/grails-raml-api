package iamedu.raml.exception.handlers

import iamedu.raml.http.RamlResponse

interface UserExceptionHandler<T> {
  RamlResponse handleException(T exception)
}
