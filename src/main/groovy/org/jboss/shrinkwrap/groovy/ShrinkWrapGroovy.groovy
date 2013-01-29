package org.jboss.shrinkwrap.groovy

import org.jboss.shrinkwrap.api.Archive
import org.jboss.shrinkwrap.api.ShrinkWrap
import org.jboss.shrinkwrap.descriptor.api.Descriptors

class ShrinkWrapGroovy {
	
	/**
	 *
	 * Helper method for creating the closures associated with each type of Archive.
	 * Since the builder basically delegates all calls to the Archive implementation the
	 * logic can be reused.
	 *
	 * @param Archive type the closure is going to handle
	 * @return A closure that accepts either a name and a build description closure or just a closure.
	 */
	public static Closure<LazyBuilder> createClosureForArchive(Class<? extends Archive> aClass) {
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
			
			def builder = new ArchiveLazyBuilder(archive)
			c.delegate = builder
			builder.appendClosures(c)
			
			//When nesting closures we still want the outermost parent to be the one resolving
			if (c.owner instanceof Closure) {
				c.@owner = c.owner.owner
			}
			return builder;
		}
	}
	
	public static Closure<LazyBuilder> createClosureForDescriptor(Class<? extends Archive> aClass) {
		// A new closure is being created and its the closure what is actually returned
		{ Object ... args ->
			def descriptor
			def c
			def realargs
			
			realargs = args.flatten() //Needed when invoked through metaclass !!
			
			if (realargs[0] instanceof String && realargs[1] instanceof Closure) {
				def name = realargs[0]
				c = realargs[1]
				descriptor = Descriptors.create(aClass, name)
			}
			else if (realargs[0] instanceof Closure) {
				c = realargs[0]
				descriptor = Descriptors.create(aClass)
			}
			else {
				throw new IllegalArgumentException()
			}
			
			def builder = new DescriptorBuilder(descriptor)
			c.delegate = builder
			builder.appendClosures(c)
			
			//When nesting closures we still want the outermost parent to be the one resolving
			if (c.owner instanceof Closure) {
				c.@owner = c.owner.owner
			}
			
			return builder
		}
	}
	
}
