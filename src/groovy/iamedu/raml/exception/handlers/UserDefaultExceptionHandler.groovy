package iamedu.raml.exception.handlers

import grails.util.GrailsUtil
import iamedu.raml.http.RamlResponse

class UserDefaultExceptionHandler implements UserExceptionHandler<Exception> {
  RamlResponse handleException(Exception exception) {
    def cause = GrailsUtil.extractRootCause(exception)
    RamlResponse.create().statusCode(500).body([
      errorCode: "unhandledError",
      received: [
        message: exception.message,
        class: exception.class.name
      ],
      original: [
        message: cause.message,
        class: cause.class.name
      ]
    ])
  }
}

