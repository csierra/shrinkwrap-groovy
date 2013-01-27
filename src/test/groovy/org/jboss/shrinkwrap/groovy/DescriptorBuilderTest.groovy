package org.jboss.shrinkwrap.groovy

import org.jboss.shrinkwrap.api.spec.EnterpriseArchive
import org.jboss.shrinkwrap.api.spec.JavaArchive
import org.jboss.shrinkwrap.api.spec.WebArchive
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.application6.ApplicationDescriptor;
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor
import org.jboss.shrinkwrap.descriptor.api.persistence20.PersistenceDescriptor;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;
import org.junit.Test

class DescriptorBuilderTest {
	
	def static ear = ShrinkWrapGroovy.createClosureForArchive(EnterpriseArchive.class)
	def static war = ShrinkWrapGroovy.createClosureForArchive(WebArchive.class)
	def static jar = ShrinkWrapGroovy.createClosureForArchive(JavaArchive.class)
	def static application = ShrinkWrapGroovy.createClosureForDescriptor(ApplicationDescriptor.class)
	def static persistence = ShrinkWrapGroovy.createClosureForDescriptor(PersistenceDescriptor.class)
	def static webXml = ShrinkWrapGroovy.createClosureForDescriptor(WebAppDescriptor.class)
	
	@Test def void testApplication() {
		assert application {
			version "6"
			displayName "datachannel_ear"
			module {
				web {
					webUri "webUri" 
					contextRoot "/data/resources"
				}
			}
			module {
				ejb "aFileName"
			}
		}.exportAsString() ==
		Descriptors.create(ApplicationDescriptor.class).version("6").displayName("datachannel_ear").
		createModule().getOrCreateWeb().webUri("webUri").contextRoot("/data/resources").up().up().
		createModule().ejb("aFileName").up().exportAsString()
	}	
	
	@Test def void testPersistence() {
		assert persistence {
			version "2.0"
			persistenceUnit {
				name "aName" 
				jtaDataSource "anotherName"
				properties {
					property {name "property"; value "value"}
				}
			}
		}.exportAsString() ==
		Descriptors.create(PersistenceDescriptor.class).version("2.0").
		getOrCreatePersistenceUnit().name("aName").jtaDataSource("anotherName").
		getOrCreateProperties().createProperty().name("property").value("value").
		up().up().up().exportAsString()
	}
	
	@Test def void testWebXml() {
		WebAppDescriptor wad = Descriptors.create(WebAppDescriptor.class)
		assert webXml {
			version "3.0"
			displayName "aName"
			sessionConfig {
				sessionTimeout 30
			}
			listener {
				description "aDescription"
				listenerClass String.class.name
			}
			filter {
				filterName "aFilter"
				filterClass "javax.servlet.HttpServlet"
			}
		}.exportAsString() ==
		wad.version("3.0").displayName("aName").createSessionConfig().sessionTimeout(30).up().
		createListener().description("aDescription").listenerClass(String.class.getName()).up().
		createFilter().filterName("aFilter").filterClass("javax.servlet.HttpServlet").up().
		exportAsString()
		
	}
}
