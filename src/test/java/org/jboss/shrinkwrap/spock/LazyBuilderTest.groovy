package org.jboss.shrinkwrap.spock

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import spock.lang.Specification;
import Extension;

class LazyBuilderTest extends Specification{

	def static jar = Extension.createClosureForArchive(JavaArchive.class)
	def static war = Extension.createClosureForArchive(WebArchive.class)
	def static ear = Extension.createClosureForArchive(EnterpriseArchive.class)
	
	
	def "test jar building"() {
		given:
			def jarDesc = jar ("JavaArchive.jar") {
				classes String.class, Integer.class
			}
			def jar2 = ShrinkWrap.create(JavaArchive.class, "JavaArchive.jar")
		when:
			def testJar = jarDesc.build()
			jar2.addClasses(String.class, Integer.class)
		then:
			testJar == jar2
	}
	
	def "test jar augmentation"() {
		given:
			def jarDesc = jar ("JavaArchive.jar") {
				classes String.class, Integer.class
			}
			def jar2 = ShrinkWrap.create(JavaArchive.class, "JavaArchive.jar")
		 
		when:
			def testJar = (jarDesc + {classes Float.class}).build()
			jar2.addClasses(String.class, Integer.class, Float.class)
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
				asLibraries jarDesc 
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
				asLibraries jar ("numbers.jar") {
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
}