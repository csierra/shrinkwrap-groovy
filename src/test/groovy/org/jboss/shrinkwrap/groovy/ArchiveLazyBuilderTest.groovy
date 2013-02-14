package org.jboss.shrinkwrap.groovy

import org.jboss.shrinkwrap.api.ArchivePaths
import org.jboss.shrinkwrap.api.ShrinkWrap
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive
import org.jboss.shrinkwrap.api.spec.JavaArchive
import org.jboss.shrinkwrap.api.spec.WebArchive
import org.jboss.shrinkwrap.descriptor.api.Descriptor;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.application6.ApplicationDescriptor
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor;

import spock.lang.Specification

class ArchiveLazyBuilderTest extends Specification{

	def static ear = ShrinkWrapGroovy.createClosureForArchive(EnterpriseArchive.class)
	def static war = ShrinkWrapGroovy.createClosureForArchive(WebArchive.class)
	def static jar = ShrinkWrapGroovy.createClosureForArchive(JavaArchive.class)
	def static application = ShrinkWrapGroovy.createClosureForDescriptor(ApplicationDescriptor.class)
	def static beans = ShrinkWrapGroovy.createClosureForDescriptor(BeansDescriptor.class)
	
	
	def "test jar building"() {
		given:
			def jarDesc = jar("JavaArchive.jar") {
				classes String.class, Integer.class
			}
			def jar2 = ShrinkWrap.create(JavaArchive.class, "JavaArchive.jar")
		when:
			def testJar = jarDesc.build()
			jar2.addClasses(String.class, Integer.class)
		then:
			testJar == jar2
	}
	
	def "test war building"() {
		given:
			def warDesc = war ("WebArchive.war") {
				classes String.class
			}
			def testWar = warDesc.build()
			def war2 = ShrinkWrap.create(WebArchive.class, "WebArchive.war")
			
		when:
			war2.addClasses(String.class)

		then:
			testWar == war2
	}
	
	def "test war composition"() {
		given:
			def jarDesc = jar ("numbers.jar") {
					classes Float.class, Number.class
				}
			
			def warDesc = war ("WebArchive.war") {
				classes String.class
				asLibrary jarDesc
			}
			def war2 = ShrinkWrap.create(WebArchive.class, "WebArchive.war")
			def jar2 = ShrinkWrap.create(JavaArchive.class, "numbers.jar")
		
		when:
			def testWar = warDesc.build()
			jar2.addClasses(Float.class, Number.class)
			war2.addClasses(String.class)
			war2.addAsLibraries(jar2)
		
		then:
			testWar == war2

	}
	
	def "test war composition inline"() {
		given:
			def warDesc = war ("WebArchive.war") {
				classes String.class
				asLibrary jar ("numbers.jar") {
					classes Float.class, Number.class
				}
			}
			def war2 = ShrinkWrap.create(WebArchive.class, "WebArchive.war")
			def jar2 = ShrinkWrap.create(JavaArchive.class, "numbers.jar")
			
		when:
			def testWar = warDesc.build()
			jar2.addClasses(Float.class, Number.class)
			war2.addClasses(String.class)
			war2.addAsLibraries(jar2)
		
		then:
			testWar == war2

	}
	
	def "test ear composition inline"() {
		given:
		def earDesc = ear ("EnterpriseArchive.ear") {
			asModule war ("WebArchive.war") {
				classes String.class
				asLibrary jar ("numbers.jar") {
					classes Float.class, Number.class
				}
			}
		}
		def ear2 = ShrinkWrap.create(EnterpriseArchive.class, "EnterpriseArchive.ear")
		def war2 = ShrinkWrap.create(WebArchive.class, "WebArchive.war")
		def jar2 = ShrinkWrap.create(JavaArchive.class, "numbers.jar")
		
		when:
			def testEar = earDesc.build()
			jar2.addClasses(Float.class, Number.class)
			war2.addClasses(String.class)
			war2.addAsLibraries(jar2)
			ear2.addAsModule(war2)
		
		then:
			testEar == ear2
	}
	
	def "test closure inclusion"() {
		given:
			def spec = { classes String.class, Integer.class }
			def testJar = jar ("jar.jar") { 
				classes Double.class
				include spec
			}.build()
		when:
			def jar2 = ShrinkWrap.create(JavaArchive.class, "jar.jar")
			jar2.addClasses(Double.class, String.class, Integer.class)
		
		then:
			testJar == jar2
		
	}
	
	def "test closure addition"() {
		given:	
			def jarDesc = jar ("jar.jar") {
				classes Double.class
			}
			//Note this is appended to the same builder
			jarDesc << { classes String.class} << { classes Integer.class }
			def testJar = jarDesc.build()
		
		when:
			def jar2 = ShrinkWrap.create(JavaArchive.class, "jar.jar")
			jar2.addClasses(Double.class, String.class, Integer.class)
		
		then:
			testJar == jar2
	}
	
	def "test closure augmentation modifies in place"() {
		given:
			def warDesc = war("war.war"){
				classes String.class
			}
			def earDesc = ear("ear.ear"){
				asModule warDesc
			}
			warDesc << { classes Integer.class }
			def testEar = earDesc.build() //Should include Integer.class in the war inside
			
		when:
			def ear2 = ShrinkWrap.create(EnterpriseArchive.class, "ear.ear")
			def war2 = ShrinkWrap.create(WebArchive.class, "war.war")
			war2.addClasses(String.class, Integer.class)
			ear2.addAsModule(war2)
		then:
			warDesc.build() == war2
			testEar == ear2
			 
		
	}
	
	def "test ShrinkWrap augmentation"() {
		given:
			def namedEar1 = ShrinkWrap.ear("name.ear"){}.build()
			def namedEar2 = ShrinkWrap.create(EnterpriseArchive.class, "name.ear")
			
			def namedJar1 = ShrinkWrap.jar("name.jar") { }.build()
			def namedJar2 = ShrinkWrap.create(JavaArchive.class, "name.jar")
			
			def namedWar1 = ShrinkWrap.war("name.war"){}.build()
			def namedWar2 = ShrinkWrap.create(WebArchive.class, "name.war")
			
			
			def unnamedEar = ShrinkWrap.ear{}.build()
			def unnamedWar = ShrinkWrap.war{}.build()
			def unnamedJar = ShrinkWrap.jar{}.build()
			
			assert namedEar1 == namedEar2
			assert namedWar1 == namedWar2
			assert namedJar1 == namedJar2
			
			assert EnterpriseArchive.class.isAssignableFrom(unnamedEar.class)
			assert WebArchive.class.isAssignableFrom(unnamedWar.class)
			assert JavaArchive.class.isAssignableFrom(unnamedJar.class)
	}
	
	def "test archive with descriptors"() {
		given: 
			def appDef = application {
					module {
						ejb "peperolo.jar"
					}
				} 
			
			def earDesc = ear {
				setApplicationXML appDef as Asset
			}
			 
			appDef << {
					module {
						web {
							webUri "aUri" 
						}
					}
				}
			
			def EnterpriseArchive earFile = earDesc.build()
			
			org.jboss.shrinkwrap.api.Node n = earFile.get(ArchivePaths.create("/META-INF/application.xml"))
			assert n.getAsset().dump().contains('''<web-uri>aUri</web-uri>''')
	}
	
	def "test inline descriptor"() {
		given:
			
			def b = beans ("META-INF/beans.xml") {
					alternatives { clazz StringBuilder.class.name }
				} 
			
			BeansDescriptor bd = Descriptors.create(BeansDescriptor.class)
			bd.getOrCreateAlternatives().clazz(StringBuilder.class.getName())
			
			assert war {
					classes String.class
					asResource b as NamedAsset
				}.build().get(ArchivePaths.create("/WEB-INF/classes/META-INF/beans.xml")).
				      getAsset().content == bd.exportAsString()
			
			assert war {
				  classes String.class
				  asResource beans ("META-INF/beans.xml") {
					  			alternatives { clazz StringBuilder.class.name }
				  			} as NamedAsset
				  }.build().get(ArchivePaths.create("/WEB-INF/classes/META-INF/beans.xml")).
					getAsset().content == bd.exportAsString()
	}
			
	def "test incremental build"() {
		given:
			BeansDescriptor bd = Descriptors.create(BeansDescriptor.class)
			bd.getOrCreateAlternatives().clazz(StringBuilder.class.getName())
			
			def b = beans ("META-INF/beans.xml") {
				alternatives { clazz StringBuilder.class.name }
			}
			
			assert war {
				classes String.class
				asResource b as NamedAsset
			}.build().get(ArchivePaths.create("/WEB-INF/classes/META-INF/beans.xml")).
				  getAsset().content == bd.exportAsString()
				  
			b << { alternatives { clazz Number.class.name } }
			bd.getOrCreateAlternatives().clazz(Number.class.getName())
			
			assert war {
				classes String.class
				asResource b as NamedAsset
			}.build().get(ArchivePaths.create("/WEB-INF/classes/META-INF/beans.xml")).
				  getAsset().content == bd.exportAsString()
	}
}