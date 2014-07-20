package iamedu.raml.exception.handlers

import iamedu.raml.exception.RamlResponseValidationException

interface RamlResponseValidationExceptionHandler {
  void handleResponseValidationException(RamlResponseValidationException exception)
}

