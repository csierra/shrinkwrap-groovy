package org.jboss.shrinkwrap.groovy

import org.jboss.shrinkwrap.api.Archive
import org.jboss.shrinkwrap.api.asset.StringAsset
import org.jboss.shrinkwrap.descriptor.api.Descriptor

class ArchiveLazyBuilder extends LazyBuilder {
	
	def ArchiveLazyBuilder(Archive<?> instance) {
		super(instance)
	}
	
	//This particular case is needed because its not easy to infer dynamically
	def classes(Class<?>[] classes) {
		this.instance.addClasses(classes)
	}
	
	/**
	 * Looks for the matching method in the archive associated with this LazyBuilder.
	 * It first looks for an exact match. If no one is found then it looks for a method
	 * named add${Name}
	 *
	 * @param name name of the method
	 * @param args List of arguments passed to the method invocation
	 * @return
	 */
	def methodMissing(String name, args) {
		/* Evaluate all the LazyBuilders that we may have been passed as parameter before
		 * invoking ShrinkWrap
		 */
		try {
			args = args.collect {
				
				//FIXME: ugh...
				if (it instanceof LazyBuilder) {
					def result = it.build()
					return (result instanceof Descriptor) ? 
							new StringAsset(result.exportAsString()) : 
							result 
				}
				else {
					return it
				}
			} as Object[] //This is needed for method lookup
		}
		catch (Throwable t) {
			throw new RuntimeException(t)
		}
		
		def method = this.instance.metaClass.getMetaMethod("$name", args)
		def m
		if (method == null) {
			m = name.capitalize();
			method = this.instance.metaClass.getMetaMethod("add$m", args)
		}
		if (method == null) {
			throw new MissingMethodException(name, this.instance.class, args)
		}
		try {
			method.doMethodInvoke(this.instance, args)
		} catch (Exception e) {
			throw new RuntimeException(e)
		}
	}
}
