package org.jboss.shrinkwrap.groovy

import org.jboss.shrinkwrap.api.Archive

/**
 * Class that holds all the closure definitions. It also delegates all method invocations
 * inside the closures to the actual Archive implementation. 
 * User of this LazyBuilder needs to call <link>LazyBuilder#build()</link> for getting the
 * actual archive built. 
 * 
 * @author <a href="mailto:csierra@gmail.com">Carlos Sierra</a>
 *
 */
public abstract class LazyBuilder {
		
	def instance;
	def closures = []
	
	def LazyBuilder(instance) {
		this.instance = instance;
	}
	
	def appendClosures(Closure<?> ... c) {
		c.each {
			it.resolveStrategy = Closure.DELEGATE_FIRST
			it.delegate = this
			this.closures.add(it)
		}
	}
	
	def abstract methodMissing(String name, args)
	
	def include(Closure<?> i) {
		i.resolveStrategy = Closure.DELEGATE_FIRST
		i.delegate = this
		i()
	}
	
	def build() {
		this.closures.each {
			it()
		}
		return this.instance
	}
	
	def leftShift (Closure<?> c) { 
		this.appendClosures(c)
		return this
	}

}
