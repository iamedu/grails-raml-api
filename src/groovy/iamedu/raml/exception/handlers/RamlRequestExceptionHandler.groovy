package iamedu.raml.exception.handlers

import iamedu.raml.exception.RamlRequestException

interface RamlRequestExceptionHandler {
  RamlErrorResponse handleRequestException(RamlRequestException exception)
}
