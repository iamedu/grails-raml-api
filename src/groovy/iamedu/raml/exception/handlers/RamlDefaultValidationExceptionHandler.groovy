package iamedu.raml.exception.handlers

import iamedu.raml.exception.*
import grails.converters.JSON

class RamlDefaultValidationHandler implements RamlValidationExceptionHandler {

  @Override
  RamlErrorResponse handleValidationException(RamlValidationException exception) {
    def errorResponse = new RamlErrorResponse([message: exception.message,
                                               errorCode: "invalidRamlDefinition",
                                               errorMeta: [errors:exception.validationResults]])
  }
}

