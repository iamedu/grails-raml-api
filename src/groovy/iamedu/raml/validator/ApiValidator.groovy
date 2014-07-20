package iamedu.raml.validator

import iamedu.raml.exception.*
import java.util.*
import java.util.regex.*

import org.raml.model.*
import org.raml.parser.visitor.*
import org.raml.parser.loader.*

class ApiValidator {

  String basePath

  Raml raml
  ResourceLoader loader
  Map endpoints

  ApiValidator(Raml raml, ResourceLoader loader) {
    this.raml = raml
    this.loader = loader
    setupValidator()
  }

  def handleResource(String resource) {
    def matcher

    def entry = endpoints.find { endpoint, validator ->
      matcher = resource =~ endpoint
      matcher.matches()
    }

    if(entry) {
      def params = []
      if(matcher[0] instanceof java.util.List) {
        params = matcher[0].drop(1).collect { it }
      }
      return [entry.value, params]
    } else {
      throw new RamlRequestException("Endpoint ${resource} does not exist", resource)
    }
  }

  private def processEndpoint(String resourcePath, Resource resource, Map<ActionType, Action> actions) {
    def replacePartPattern = "([^/]*)"
    def partPattern = /\{[^\{\}]*\}/

    def regexPath = resourcePath.replaceAll(partPattern, replacePartPattern)
    def params = (resourcePath =~ partPattern).collect { it }

    def pattern = Pattern.compile(basePath + regexPath)
    
    endpoints.put(pattern, new EndpointValidator(raml, resourcePath, resource, params, actions))
  }

  private def processEndpoints(String prefix, Map resources) {
    resources.each { key, resource ->
      def currentPrefix  = "${prefix}${key}"
      if(resource.actions.size() > 0) {
        processEndpoint(currentPrefix.toString(), resource, resource.actions)
      }
      processEndpoints(currentPrefix.toString(), resource.resources)
    }
  }

  private def setupValidator() {
    endpoints = new HashMap()

    basePath = raml.basePath

    if(basePath.endsWith("/")) {
      basePath = basePath.substring(0, basePath.length() - 1)
    }

    processEndpoints("", raml.resources)
  }

}

