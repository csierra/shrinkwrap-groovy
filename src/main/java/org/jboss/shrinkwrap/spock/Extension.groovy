package org.jboss.shrinkwrap.spock

import org.jboss.shrinkwrap.api.Archive
import org.jboss.shrinkwrap.api.ShrinkWrap
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive
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
		augmentOperator()
	}
	
	/**
	 * 
	 * Helper method for creating the closures associated with each type of Archive. 
	 * Since the builder basically delegates all calls to the Archive implementation the
	 * logic can be reused.
	 * 
	 * @param Archive type the closure is going to handle 
	 * @return A closure that accepts either a name and a build description closure or just a closure. 
	 */
	private static Closure<LazyBuilder> createClosureForArchive(Class<? extends Archive> aClass) {
		// A new closure is being created and its the closure what is actually returned
		{ Object ... args ->
			def archive
			def c
			def realargs
			
			realargs = args.flatten() //Needed when invoked through metaclass !!
			
			if (realargs[0] instanceof String && realargs[1] instanceof Closure) {
				def name = realargs[0]
				c = realargs[1]
				archive = ShrinkWrap.create(aClass, name)
			}
			else if (realargs[0] instanceof Closure) {
				c = realargs[0]
				archive = ShrinkWrap.create(aClass)
			}
			else {
				throw new IllegalArgumentException()
			}
			
			def builder = new LazyBuilder(archive)
			c.delegate = builder
			builder.appendClosures(c)
			
			//When nesting closures we still want the outermost parent to be the one resolving 
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
		//Nothing to be done here per specification
	}
	
}
