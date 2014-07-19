package iamedu.raml.exception.handlers

import iamedu.raml.exception.*
import grails.converters.JSON

class RamlDefaultRequestExceptionHandler implements RamlRequestExceptionHandler {

  @Override
  RamlErrorResponse handleRequestException(RamlRequestException exception) {
    def jsonError = new java.util.HashMap(exception.jsonError)
    jsonError.requestBody = JSON.parse(exception.body)
    def errorResponse = new RamlErrorResponse([message: exception.message,
                                               errorCode: "invalidRequest",
                                               errorMeta: jsonError])
    errorResponse
  }
}
