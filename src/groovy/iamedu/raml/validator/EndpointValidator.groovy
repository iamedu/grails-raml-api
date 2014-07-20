package iamedu.raml.validator

import iamedu.raml.exception.*

import org.apache.commons.lang.WordUtils
import org.commonjava.mimeparse.MIMEParse
import org.eel.kitchen.jsonschema.main.*
import org.eel.kitchen.jsonschema.util.JsonLoader

import org.raml.model.*
import org.raml.parser.visitor.*
import org.raml.parser.loader.*

import java.util.*

import grails.converters.JSON

class EndpointValidator {
  Raml raml
  
  String serviceName
  String path
  List<String> params

  Resource resource
  Map<String, Action> actions

  EndpointValidator(Raml raml, String path, Resource resource, List<String> params, Map<String, Action> actions) {
    this.raml     = raml
    this.path     = path
    this.params   = params
    this.resource = resource
    this.actions  = actions.collectEntries { k, v ->
      [k.toString(), v]
    }

    setup()
  }

  def handleResponse(def request, def response) {
    def error = null
    def action = actions.get(request.method.toUpperCase())
    def ramlResponse = action.getResponses().get("${response.statusCode}".toString())
    def contentType = response.contentType.split(";")[0]
    def method = request.method.toLowerCase()

    if(ramlResponse) {
      def mimeType = ramlResponse.body.get(contentType)
      if(mimeType) {
        if(mimeType.schema) {
          def schemaFormat = JsonLoader.fromString(raml.consolidatedSchemas.get(mimeType.schema))
          def factory = JsonSchemaFactory.defaultFactory()

          def schema = factory.fromSchema(schemaFormat)

          def stringBody = (response.body as JSON).toString()
          jsonBody = JsonLoader.fromString(stringBody)
          def report =  schema.validate(jsonBody)

          if(!report.isSuccess()) {
            throw new RamlResponseValidationException("Invalid content type ${contentType} replied from ${request.serviceName} ${method}",
              request.serviceName,
              method,
              response.statusCode,
              contentType,
              stringBody,
              RamlResponseValidationException.ErrorReason.INVALID_BODY,
              report)
          }
        }
      } else {
        throw new RamlResponseValidationException("Invalid content type ${contentType} replied from ${request.serviceName} ${method}",
          request.serviceName,
          method,
          response.statusCode,
          contentType,
          response.body.toString(),
          RamlResponseValidationException.ErrorReason.INVALID_MIME_TYPE)
      }
    } else {
      throw new RamlResponseValidationException("Invalid status code ${response.statusCode} replied from ${request.serviceName} ${method}",
        request.serviceName,
        method,
        response.statusCode,
        contentType,
        response.body.toString(),
        RamlResponseValidationException.ErrorReason.INVALID_STATUS_CODE)
    }
  }

  def handleRequest(def request) {
    if(!supportsMethod(request.method)) {
      throw new RamlRequestException("Method ${request.method} for endpoint ${resource} does not exist", request.forwardURI, request.method)
    }

    def action = actions.get(request.method.toUpperCase())
    def jsonBody
    def bestMatch

    if(action.hasBody()) {
      bestMatch = MIMEParse.bestMatch(action.body.keySet(), request.getHeader("Accept"))
      def mimeType

      if(bestMatch) {
        mimeType = action.body.get(bestMatch)
      } else {
        throw new RamlRequestException("Unable to find a matching mime type for ${path}", path, request.method)
      }

      if(mimeType.schema) {
        def schemaFormat = JsonLoader.fromString(raml.consolidatedSchemas.get(mimeType.schema))
        def factory = JsonSchemaFactory.defaultFactory()

        def schema = factory.fromSchema(schemaFormat)

        def stringBody = request.JSON.toString()
        jsonBody = JsonLoader.fromString(stringBody)
        def report =  schema.validate(jsonBody)

        if(!report.isSuccess()) {
          throw new RamlRequestException("Invalid body ${stringBody} for resource ${path} method ${request.method}",
            path,
            request.method,
            report,
            stringBody)
        }
      }
    }

    def headers = request.headerNames.toList().collectEntries {
      [it, request.getHeaders(it).toList()]
    }

    def result = [
      hasBody: action.hasBody(),
      serviceName: serviceName,
      jsonBody: jsonBody,
      params: params,
      accept: bestMatch,
      method: request.method,
      headers: headers
    ]

    result
  }

  boolean supportsMethod(String method) {
    method = method.toUpperCase()

    actions.containsKey(method)
  }

  private def setup() {
    if(!resource.displayName) {
      throw new IllegalArgumentException("Resource ${path} has no display name defined")
    }

    def firstChar = "${resource.displayName.charAt(0)}".toLowerCase()

    serviceName = WordUtils
      .capitalizeFully(resource.displayName)
      .replaceAll(" ", "")
      .replaceFirst(".", firstChar)
    serviceName = serviceName + "Service"
  }

}

