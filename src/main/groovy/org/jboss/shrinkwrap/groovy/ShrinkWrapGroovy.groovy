package org.jboss.shrinkwrap.groovy

import org.jboss.shrinkwrap.api.Archive
import org.jboss.shrinkwrap.api.ShrinkWrap
import org.jboss.shrinkwrap.descriptor.api.Descriptors

class ShrinkWrapGroovy {
	
	/**
	 * Helper closure for creating the closures associated with a builder, a factory and a wrapped instance
	 * Since the builder basically delegates all calls to the instance the
	 * logic can be reused.
	 *
	 */
	
	private static createGenericClosure = { mix, Class<?> factory, Class<?> aClass ->
		//This returns the closure 
		{ Object ... args ->
			def instance
			def c
			def realargs
			
			realargs = args.flatten() //Needed when invoked through metaclass !! https://jira.codehaus.org/browse/GROOVY-5009
			
			if (realargs[0] instanceof String && realargs[1] instanceof Closure) {
				def name = realargs[0]
				c = realargs[1]
				instance = factory.create(aClass, name)
			}
			else if (realargs[0] instanceof Closure) {
				c = realargs[0]
				instance = factory.create(aClass)
			}
			else {
				throw new IllegalArgumentException()
			}
			
			instance.metaClass.mixin mix
			executeClosureonDelegate(instance, c)
			return instance
		}
	}
	
	protected static executeClosureonDelegate(delegate, Closure<?> closure) {
		def Closure<?> c = closure.clone()
		c.delegate = delegate
		c()
	}
	
	public static createClosureForDescriptor = createGenericClosure.curry(DescriptorBuilder.class, Descriptors.class)
	public static createClosureForArchive = createGenericClosure.curry(ArchiveLazyBuilder.class, ShrinkWrap.class)
}
