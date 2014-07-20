// configuration for plugin testing - will not be included in the plugin zip
grails.project.repos.bintray.url = 'https://api.bintray.com/maven/iamedu/maven/raml-api'
grails.project.repos.default = 'bintray'

log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}
    info 'iamedu.raml'

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'
}

environments {
  development {
    iamedu.raml.ramlExportUrl = '/api/raml/'
    iamedu.raml.ramlDefinition = 'jukebox-api.raml'
    iamedu.raml.strictMode = true
    iamedu.raml.serveExamples = true
    iamedu.raml.reload = true
  }
}
