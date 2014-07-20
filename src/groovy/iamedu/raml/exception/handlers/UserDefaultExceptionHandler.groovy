package iamedu.raml.exception.handlers

import grails.util.GrailsUtil
import iamedu.raml.http.RamlResponse

class UserDefaultExceptionHandler implements UserExceptionHandler<Exception> {
  RamlResponse handleException(Exception exception) {
    def cause = GrailsUtil.extractRootCause(exception)
    def responseBody = [
      errorCode: "unhandledError",
      exception: [
        message: exception.message,
        class: exception.class.name
      ]
    ]
    if(exception != cause) {
      responseBody.cause = [
        message: cause.message,
        class: cause.class.name
      ]
    }
    RamlResponse.create().statusCode(500).body(responseBody)
  }
}

