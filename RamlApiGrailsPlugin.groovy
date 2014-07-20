import iamedu.raml.exception.handlers.*
import iamedu.raml.security.*

import grails.converters.JSON

import org.springframework.aop.scope.ScopedProxyFactoryBean

class RamlApiGrailsPlugin {
    // the plugin version
    def version = "0.1.0"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.4 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "Raml Api Plugin" // Headline display name of the plugin
    def author = "Eduardo Díaz Real"
    def authorEmail = "iamedu@gmail.com"
    def description = '''\
    Allows to use a raml api file description file and generates automatically the clients.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/raml-api"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
      println "Setting up raml api plugin"
      ramlValidationExceptionHandler(RamlDefaultValidationHandler) {
      }

      ramlRequestExceptionHandler(RamlDefaultRequestExceptionHandler) {
      }

      ramlSecurityExceptionHandler(RamlDefaultSecurityExceptionHandler) {
      }

      generalExceptionHandler(UserDefaultExceptionHandler) {
      }

      securityHandler(ScopedProxyFactoryBean) {
        targetBeanName = 'ramlSecurityHandler'
        proxyTargetClass = true
      }

      ramlSecurityHandler(DefaultSecurityHandler) { bean ->
        bean.scope = 'session'
      }
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { ctx ->
        JSON.registerObjectMarshaller(com.fasterxml.jackson.databind.node.ObjectNode) {
          JSON.parse(it.toString())
        }
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
