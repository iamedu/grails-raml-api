package iamedu.raml.exception.handlers

import iamedu.raml.exception.RamlValidationException

interface RamlValidationExceptionHandler {
  RamlErrorResponse handleValidationException(RamlValidationException exception)
}
