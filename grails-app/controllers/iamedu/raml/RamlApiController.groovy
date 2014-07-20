package iamedu.raml

import iamedu.raml.exception.handlers.*
import iamedu.raml.exception.*
import iamedu.raml.security.*

import grails.converters.JSON
import grails.util.Holders

class RamlApiController {

  def grailsApplication
  SecurityHandler securityHandler
  RamlHandlerService ramlHandlerService
  RamlValidationExceptionHandler ramlValidationExceptionHandler
  RamlRequestExceptionHandler ramlRequestExceptionHandler
  RamlSecurityExceptionHandler ramlSecurityExceptionHandler
  def config = Holders.config

  def handle() {
    def jsonRequest = request.JSON

    def validator = ramlHandlerService.buildValidator()
    def (endpointValidator, paramValues) = validator.handleResource(request.forwardURI)

    def request = endpointValidator.handleRequest(request)
    def methodName = request.method.toLowerCase()

    if(!securityHandler.userAuthenticated()) {
      throw new RamlSecurityException("User has not been authenticated")
    }

    if(!securityHandler.authorizedExecution(request.serviceName, methodName)) {
      throw new RamlSecurityException("User has no permission to access service ${request.serviceName} method ${methodName}",
        request.serviceName,
        methodName)
    }

    def service
    def result

    if(grailsApplication.mainContext.containsBean(request.serviceName)) {
      service = grailsApplication.mainContext.getBean(request.serviceName)
    }

    if(service) {
      def methods = service.class.getMethods().grep {
        it.name == methodName
      }

      if(methods.size() == 1) {
        def method = methods.first()

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
          result = method.invoke(service, invokeParams as Object[])
          try {
            endpointValidator.handleResponse(request, result)
          } catch(RamlResponseValidationException ex) {
            def beans = grailsApplication.mainContext.getBeansOfType(RamlResponseValidationExceptionHandler.class)
            beans.each {
              it.handleResponseValidationException(ex)
            }
            if(config.iamedu.raml.strictMode) {
              throw ex
            }
          }
        }
      } else if(methods.size() > 1) {
        throw new RuntimeException("Only one method can be named ${methodName} in service ${request.serviceName}")
      } else {
        if(!config.iamedu.raml.serveExamples) {
          throw new RuntimeException("No method named ${methodName} in service ${request.serviceName}")
        }
      }
    } else {
      if(!config.iamedu.raml.serveExamples) {
        throw new RuntimeException("No service name ${request.serviceName} exists")
      }
    }
    
    log.debug "About to invoke service ${request.serviceName} method $request.method}"

    if(result == null && config.iamedu.raml.serveExamples) {
      result = endpointValidator.generateExampleResponse(request)
    }

    response.status = result.statusCode

    if(result.contentType?.startsWith("application/json")) {
      render result.body as JSON
    } else {
      render result.body
    }
  }

  def handleRamlSecurityException(RamlSecurityException ex) {

    if(ex.serviceName) {
      response.status = 403
    } else {
      response.status = 401
    }

    def errorResponse = ramlSecurityExceptionHandler.handleSecurityException(ex)

    render errorResponse as JSON
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

  def handleException(Exception ex) {
    def handler = determineHandler(ex)
    def errorResponse = handler.handleException(ex)

    response.status = errorResponse.statusCode

    log.error ex.message, ex
    if(errorResponse.contentType?.startsWith("application/json")) {
      render errorResponse.body as JSON
    } else {
      render errorResponse.body
    }
  }

  private def determineHandler(Throwable ex) {
    Integer depth = Integer.MAX_VALUE
    def handler = null
    for(def entry : grailsApplication.mainContext.getBeansOfType(UserExceptionHandler.class).entrySet()) {
      def type = entry.value.getClass().getGenericInterfaces().grep {
        it instanceof java.lang.reflect.ParameterizedType
      }.first().actualTypeArguments.first()

      Integer currentDepth = 0
      Throwable t = ex
      while(t && t.class != Throwable.class && t.class != type) {
        currentDepth += 1
        t = t.cause
      }

      if(currentDepth < depth) {
        handler = entry.value
      }
    }
    handler
  }

}
