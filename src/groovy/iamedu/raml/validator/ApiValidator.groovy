package iamedu.raml.validator

import iamedu.raml.exception.*
import java.util.*
import java.util.regex.*

import org.raml.model.*
import org.raml.parser.visitor.*
import org.raml.parser.loader.*
import grails.util.Holders

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap

class ApiValidator {

  def config = Holders.config
  
  String basePath

  Integer maxCacheCapacity = 5000
  Raml raml
  ResourceLoader loader
  Map endpoints
  Map entryCache

  ApiValidator(Raml raml, ResourceLoader loader) {
    this.raml = raml
    this.loader = loader
    entryCache = new ConcurrentLinkedHashMap.Builder()
      .maximumWeightedCapacity(maxCacheCapacity)
      .build()
    setupValidator()
  }

  def handleResource(String resource) {

    def entry = null
    def reloadRaml = config.iamedu.raml.reload

    entry = entryCache.get(resource)

    if(!entry) {
      entry = endpoints.find { endpoint, validator ->
        def matcher = resource =~ endpoint
        matcher.matches()
      }
      if(!reloadRaml) {
        entryCache.put(resource, entry)
      }
    }

    if(entry) {
      def params = []
      def matcher = resource =~ entry.key
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

