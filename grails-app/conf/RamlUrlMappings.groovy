import org.apache.commons.logging.LogFactory

class RamlUrlMappings {
  
	static mappings = { applicationContext ->
    def logger = LogFactory.getLog(RamlUrlMappings)

    def patternList = applicationContext.grailsApplication.config.iamedu.raml.mappings

    if(!patternList) {
      patternList = ['/api']
    }

    logger.info "Setting url mappings for RAML controller:"

    patternList.each { pattern ->
      "${pattern}"(controller:"ramlApi")
      "${pattern}/**"(controller:"ramlApi")
      logger.info("${pattern}")
      logger.info("${pattern}/**")
    }
	}
}
