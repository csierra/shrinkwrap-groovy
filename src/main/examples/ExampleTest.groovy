import org.jboss.shrinkwrap.api.Archive

import spock.lang.Specification


class ExampleTest extends Specification {
	
	 public static Archive<?> getDeployment() {
        new ExampleDeploy().with {
            warDesc << { classes String.class }
            webAppDesc << { 
				servlet {
					servletClass String.class.name
					servletName "My Servlet"			
				}
			}
	    build()
        }
    }
	 
	def static main(args) {
		println getDeployment()
	}
}
