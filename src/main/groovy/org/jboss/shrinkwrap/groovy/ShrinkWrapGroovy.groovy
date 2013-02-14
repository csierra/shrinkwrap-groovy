package org.jboss.shrinkwrap.groovy

import org.jboss.shrinkwrap.api.Archive
import org.jboss.shrinkwrap.api.ShrinkWrap
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.descriptor.api.Descriptors
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor;

class ShrinkWrapGroovy {
	
	/**
	 * Helper closure for creating the closures associated with a builder, a factory and a wrapped instance
	 * Since the builder basically delegates all calls to the instance the
	 * logic can be reused.
	 *
	 */
	
	private static <T> Closure<LazyBuilder<T>> createGenericClosure (Class<LazyBuilder<T>> builderClass, Class<?> factory, Class<T> aClass) {
		//This returns the closure 
		{ Object ... args ->
			def instance
			def Closure<T> c
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
			
			def builderInstance = builderClass.newInstance(instance)
			builderInstance.appendClosures(c)
			
			return builderInstance
		}
	}
	
	public static createClosureForDescriptor = ShrinkWrapGroovy.&createGenericClosure.curry(DescriptorBuilder.class, Descriptors.class)
	public static <T extends Archive<T>> Closure<LazyBuilder<ArchiveLazyBuilder<T>>> createClosureForArchive (Class<T> aClass) {
		createGenericClosure(ArchiveLazyBuilder.class, ShrinkWrap.class, aClass)
	}
}
