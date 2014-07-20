package iamedu.raml

import iamedu.raml.exception.handlers.*
import iamedu.raml.exception.*

import grails.converters.JSON

class RamlApiController {

  def grailsApplication
  RamlHandlerService ramlHandlerService
  RamlValidationExceptionHandler ramlValidationExceptionHandler
  RamlRequestExceptionHandler ramlRequestExceptionHandler

  def handle() {
    def jsonRequest = request.JSON

    def validator = ramlHandlerService.buildValidator()
    def (endpointValidator, paramValues) = validator.handleResource(request.forwardURI)

    def request = endpointValidator.handleRequest(request)
    def methodName = request.method.toLowerCase()
    def service = grailsApplication.mainContext.getBean(request.serviceName)
    def methods = service.class.getMethods().grep {
      it.name == methodName
    }
    def method = methods.first()
    def result

    def params = [request.params, paramValues].transpose().collectEntries {
      [it[0].replaceAll("\\{|\\}", ""), it[1]]
    }

    if(method.parameterTypes.size() == 0) {
      result = method.invoke(service)
    } else {
      def invokeParams = []
      method.parameterTypes.eachWithIndex { it, i ->
        def param
        def annotation =  method.parameterAnnotations[i].find {
          it.annotationType() == iamedu.raml.http.RamlParam
        }
        if(annotation) {
          def parameterName = annotation.value()
          def paramValue = params[parameterName]
          param = paramValue.asType(it)
        } else if(Map.isAssignableFrom(it)) {
          param = JSON.parse(request.jsonBody.toString())
        }
        invokeParams.push(param)
      }
      println invokeParams
      result = method.invoke(service, invokeParams as Object[])
    }

    

    log.debug "About to invoke service ${request.serviceName} method $request.method}"

    response.status = result.statusCode

    if(result.contentType?.startsWith("application/json")) {
      render result.body as JSON
    } else {
      render result.body
    }
  }

  def handleRamlRequestException(RamlRequestException ex) {
    response.status = 400

    def errorResponse = ramlRequestExceptionHandler.handleRequestException(ex)

    render errorResponse as JSON
  }

  def handleRamlValidationException(RamlValidationException ex) {
    response.status = 500

    log.error "Invalid raml definition", ex
    def errorResponse = ramlValidationExceptionHandler.handleValidationException(ex)

    render errorResponse as JSON
  }

}
