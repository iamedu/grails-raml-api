package iamedu.raml.exception.handlers

import iamedu.raml.exception.RamlSecurityException

interface RamlSecurityExceptionHandler {
  RamlErrorResponse handleSecurityException(RamlSecurityException exception)
}

