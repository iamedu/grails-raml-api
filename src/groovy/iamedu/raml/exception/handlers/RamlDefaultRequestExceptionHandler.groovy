package iamedu.raml.exception.handlers

import iamedu.raml.exception.*
import grails.converters.JSON

class RamlDefaultRequestExceptionHandler implements RamlRequestExceptionHandler {

  @Override
  RamlErrorResponse handleRequestException(RamlRequestException exception) {
    def jsonError
    if(exception.jsonError) {
      jsonError = new java.util.HashMap(exception.jsonError)
    } else {
      jsonError = new java.util.HashMap()
    }
    if(exception.body) {
      jsonError.requestBody = JSON.parse(exception.body)
    }
    def errorResponse = new RamlErrorResponse([message: exception.message,
                                               errorCode: "invalidRequest",
                                               errorMeta: jsonError])
    errorResponse
  }
}
