package org.jboss.shrinkwrap.spock

import org.jboss.shrinkwrap.api.Archive
import org.jboss.shrinkwrap.api.ShrinkWrap
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive
import org.jboss.shrinkwrap.groovy.ShrinkWrapGroovy;
import org.spockframework.runtime.extension.IGlobalExtension
import org.spockframework.runtime.model.SpecInfo

/**
 * ShrinkWrap extension to the Spock testing framework
 * 
 * @author <a href="mailto:csierra@gmail.com">Carlos Sierra</a>
 *
 */
class Extension implements IGlobalExtension {
	
	static {
		augmentShrinkwrap()
	}
	
	
	private static void augmentShrinkwrap() {
		ShrinkWrap.metaClass.static.ear = ShrinkWrapGroovy.createClosureForArchive(EnterpriseArchive.class)
		ShrinkWrap.metaClass.static.war = ShrinkWrapGroovy.createClosureForArchive(WebArchive.class)
		ShrinkWrap.metaClass.static.jar = ShrinkWrapGroovy.createClosureForArchive(JavaArchive.class)
	}
	
	public void visitSpec(SpecInfo spec) {
		//Nothing to be done here per specification
	}
	
}
