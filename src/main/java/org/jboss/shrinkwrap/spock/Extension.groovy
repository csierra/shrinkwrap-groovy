package org.jboss.shrinkwrap.spock

import org.jboss.shrinkwrap.api.Archive
import org.jboss.shrinkwrap.api.ShrinkWrap
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive
import org.spockframework.runtime.extension.IGlobalExtension
import org.spockframework.runtime.model.SpecInfo

class Extension implements IGlobalExtension {
	
	static {
		augmentShrinkwrap()
		augmentOperator()
	}
	
	private static Closure<LazyBuilder> createClosureForArchive(Class<? extends Archive> aClass) {
		{ String name, Closure c ->
			def archive = ShrinkWrap.create(aClass, name)
			def builder = new LazyBuilder(archive)
			c.delegate = builder
			builder.appendClosures(c)
			if (c.owner instanceof Closure) {
				c.@owner = c.owner.owner
			}
			return builder;
		}
	}
	
	private static void augmentShrinkwrap() {
		ShrinkWrap.metaClass.static.ear = createClosureForArchive(EnterpriseArchive.class)
		ShrinkWrap.metaClass.static.war = createClosureForArchive(WebArchive.class)
		ShrinkWrap.metaClass.static.jar = createClosureForArchive(JavaArchive.class)
	}
	
	private static def augmentOperator() {
		LazyBuilder.metaClass.plus = { Closure c -> 
			delegate.appendClosures(c)
			return delegate
		}
	}

	public void visitSpec(SpecInfo spec) {
		//Nothing to be done here
	}
	
}
