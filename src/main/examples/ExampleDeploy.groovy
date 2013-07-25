import org.jboss.shrinkwrap.api.spec.EnterpriseArchive
import org.jboss.shrinkwrap.api.spec.JavaArchive
import org.jboss.shrinkwrap.api.spec.WebArchive
import org.jboss.shrinkwrap.descriptor.api.application6.ApplicationDescriptor;
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;
import org.jboss.shrinkwrap.groovy.ShrinkWrapGroovy


class ExampleDeploy {
	
	def static ear = ShrinkWrapGroovy.createClosureForArchive(EnterpriseArchive.class)
	def static war = ShrinkWrapGroovy.createClosureForArchive(WebArchive.class)
	def static jar = ShrinkWrapGroovy.createClosureForArchive(JavaArchive.class)
	
	def static beans = ShrinkWrapGroovy.createClosureForDescriptor(BeansDescriptor.class)
	def static webXml = ShrinkWrapGroovy.createClosureForDescriptor(WebAppDescriptor.class)
	def static application = ShrinkWrapGroovy.createClosureForDescriptor(ApplicationDescriptor.class)
	
	def otherClasses = {
		classes Double.class, Float.class
	}
	
	def webAppDesc = webXml {
			version "3.0"
			sessionConfig {
				sessionTimeout 30
			}
		}
	
	def warDesc = war {
		
		asLibrary jar ('myjar.jar') {
			classes String.class, Integer.class
		}
		
		include otherClasses
		
		asResource beans {
			alternatives {
				clazz StringBuilder.class.name
			}
		}.resource("META-INF/beans.xml")
		
		asWebInfResource webAppDesc, "web.xml"
	}
	
	def earDesc = ear {
		asModule warDesc
		setApplicationXML application {
			module {
				web {
					webUri "aUri"
					contextRoot "aContextRoot"
				}
			}
		} 
	}
	
	def build() {
		earDesc.build()
	}
	
}

