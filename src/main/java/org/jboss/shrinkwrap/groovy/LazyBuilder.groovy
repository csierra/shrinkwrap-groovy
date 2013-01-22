package org.jboss.shrinkwrap.groovy

import org.jboss.shrinkwrap.api.Archive

/**
 * Class that holds all the closure definitions. It also delegates all method invocations
 * inside the closures to the actual Archive implementation
 * 
 * @author <a href="mailto:csierra@gmail.com">Carlos Sierra</a>
 *
 */
public class LazyBuilder {
		
	def Archive<?> instance;
	def closures = []
	
	def LazyBuilder(Archive<?> instance) {
		this.instance = instance;
	}
	
	def appendClosures(Closure<?> ... c) {
		c.each {
			it.delegate = this
			this.closures.add(it)
		}
	}
	
	//This particular case is needed because its not easy to infer dinamycally
	def classes(Class<?>[] classes) {
		this.instance.addClasses(classes)
	}
	
	/**
	 * Looks for the matching method in the archive associated with this LazyBuilder
	 * 
	 * @param name name of the method
	 * @param args List of arguments passed to the method invocation
	 * @return
	 */
	def invokeMethod(String name, args) {
		/* Evaluate all the LazyBuilders that we may have been passed as parameter before
		 * invoking ShrinkWrap
		 */
		args = args.collect {
			if (it instanceof LazyBuilder) {
				return it.build()
			}
			else {
				return it
			}
		} as Object[] //This is needed for method lookup
		
		def method = this.instance.metaClass.getMetaMethod("$name", args)
		def m 
		if (method == null) {
			m = name.capitalize();
			method = this.instance.metaClass.getMetaMethod("add$m", args)
		}
		if (method == null) {
			throw new RuntimeException("Could not find method add$m($args) or $name($args) on "+this.instance.class)
		}
		method.doMethodInvoke(this.instance, args)
	}
	
	def include(Closure<?> i) {
		i.delegate = this
		i()
	}
	
	def build() {
		this.closures.each {
			it()
		}
		return this.instance
	}
	
	def leftShift (Closure c) { 
		this.appendClosures(c)
		return this
	}

}
