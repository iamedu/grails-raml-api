package iamedu.raml.exception.handlers

import iamedu.raml.exception.*

class RamlDefaultSecurityExceptionHandler implements RamlSecurityExceptionHandler {

  @Override
  RamlErrorResponse handleSecurityException(RamlSecurityException exception) {
    String message
    String errorCode
    if(exception.serviceName) {
      message = "The user has no permission to execute this service and method"
      errorCode = "permissionInvalid"
    } else {
      message = "The user is not authenticated"
      errorCode = "authenticationInvalid"
    }
    def errorResponse = new RamlErrorResponse([message: message,
                                               errorCode: errorCode,
                                               errorMeta: [
                                                serviceName: exception.serviceName,
                                                method: exception.method
                                               ]])
    errorResponse
  }
}


