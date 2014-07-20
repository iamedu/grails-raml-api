package iamedu.raml

import iamedu.raml.validator.*
import org.raml.parser.loader.*

import grails.util.Holders
import net.sf.jmimemagic.Magic

class RamlDocumentationController {

  ResourceLoader loader
  def config = Holders.config

  RamlDocumentationController() {
    loader = new GrailsResourceLoader()
  }

  def handle() {
    def ramlExportUrl = config.iamedu.raml.ramlExportUrl
    def requestUrl = "/raml" + request.forwardURI.replaceFirst(request.contextPath, "").replaceFirst(ramlExportUrl, "")
    def file = loader.fetchResource(requestUrl)?.bytes
    if(!file) {
      response.status = 404
    } else {
      render file: file, contentType: Magic.getMagicMatch(file).mimeType
    }
  }

}
