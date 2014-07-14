package iamedu.raml.validator

import java.util.*

import org.raml.model.*
import org.raml.parser.visitor.*
import org.raml.parser.loader.*

class EndpointValidator {
  String path
  List<String> params

  List serviceCalls
  Resource resource
  Map<String, Action> actions

  EndpointValidator(String path, Resource resource, List<String> params, Map<String, Action> actions) {
    this.path     = path
    this.params   = params
    this.resource = resource
    this.actions  = actions

    Resource curr = resource
    serviceCalls = new ArrayList()
    while(curr != null) {
      serviceCalls.add(0, curr)
      curr = curr.parentResource
    }
    println serviceCalls
  }
}

