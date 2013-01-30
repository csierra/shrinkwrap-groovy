Shrinkwrap Groovy DSL
=====================

Helper for describing ShrinkWrap archives using Groovy closures. This will allow to reuse Archive and Descriptors descriptions, augment and compose them. 

For example:
`````groovy
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
		}, "META-INF/beans.xml"
		
		asWebInfResource , "web.xml"
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
`````

This would be a description for a base deployment. Then you can use this from your test:
`````groovy
@RunWith(Arquillian.class)
public class Example {

    @Deployment public static Archive<?> getDeployment() {
        new ExampleDeploy().with {
            warDesc << { classes MyServlet.class }
            webAppDesc << { 
				servlet {
					servletClass MyServlet.class.name
					servletName "My Servlet"			
				}
			}
	    build()
        }
    }
}
`````
